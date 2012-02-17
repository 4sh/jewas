function ContentCreator(type, extensions, extensionsMsgError, previsualizationContainerId, createPrevisualizationObject, saveButton, initialContent) {
    var uploadedFiles = [];
    var currentUploadedFileId = "";
    var contentCreator = this;
    /**
     * Whether the uploaded file will need to be post-processed server side.
     * (Only used for video files)
     */
    var postProcess = false;


    /** Private methods **/
    function getDomains(domainIds) {
        var domains = [];

        if (domainIds !== null) {
            for (var i=0; i < domainIds.length; i++) {
                domains.push({id: domainIds[i]});
            }
        }
        return domains;
    }

    function getConnectedUserNames(container) {
        $.getJSON(
            '/connectedUser',
            function (data) {
                $(container).children().remove();
                $("#authorItemTemplate").tmpl(data).appendTo(container);
            });
    }

    function isNullOrEmpty(stringValue) {
        return stringValue === null || stringValue === "";
    }

    /**
     * Is submit operation is permitted in the current content state.
     *
     * @param contentVersion the edited content version, null if content creation.
     */
    function isSubmitAuthorized() {
        var contentTitle = $("#title").val();
        var contentDescription = $("#description").val();

        var requiredDataCompleted = !isNullOrEmpty(contentTitle) && !isNullOrEmpty(contentDescription);

        /* In case of first version of content, forbids empty content */
        if (initialContent) {
            return requiredDataCompleted && !isNullOrEmpty(currentUploadedFileId);
        } else {
            return requiredDataCompleted;
        }
    }

    function saveContent(form) {
        var contentDetail = {
            header: {
                title: $("#title").val(),
                description: $("#description").val(),
                domains: getDomains($("#domains").val()),
                tags: $("#tags").val()
            }
        }

        var dataToSend = {
            type : type,
            contentDetail : JSON.stringify(contentDetail)
        };

        $.ajaxPut(form.action,
            dataToSend,
            function(data){
                var postUrl = "/content/" + data.id + "/content/" + currentUploadedFileId;
                if (postProcess) {
                    postUrl += "?postProcess=1";
                }
                $.post(postUrl,
                    null,
                    function () {
                        $("#saveSuccessDialog").dialog('open');
                        setTimeout(function(){
                            $("#saveSuccessDialog").dialog('close');
                            window.location.href = "/content/" + data.id + "/view.html";
                        }, 2000);
                    }
                );
            }
        );
        return false;
    }

    /** Public methods **/

    /**
     * Refreshes the submit form button state depending on the required field values.
     * Called on each mandatory field value change event.
     *
     * @param contentVersion the edited content version.
     */
    this.refreshSubmitButton = function() {
        if (isSubmitAuthorized()) {
            saveButton.removeAttr('disabled');
        } else {
            saveButton.attr('disabled', '');
        }
    }

    /**
     * Setter on post process property.
     *
     * @param _postProcess the boolean value of process
     */
    this.setPostProcess = function (_postProcess) {
        postProcess = _postProcess;
        return this;
    }

    this.removeUploadedFiles = function () {
        $.ajaxDelete(
            '/upload',
             {fileNames : JSON.stringify(uploadedFiles)}
        );
    };

    /** Constructor **/
    (function () {
        $("#confirmationDialog").dialog({
            autoOpen: false,
            show: 'slide',
            hide: 'slide',
            width: '40%',
            buttons: [
                {
                    text: "Ok",
                    click:function () {
                        var form = $("#createContent")[0];
                        if (!!form) {
                            saveContent(form);
                        } else {
                            console.error("Failed to locate 'HTML FORM' element");
                        }
                        $(this).dialog("close");
                    }
                },
                {
                    text: "Annuler",
                    click: function() {
                        $(this).dialog("close");
                    }
                }
            ]
        });

        $("#saveSuccessDialog").dialog({
            autoOpen: false,
            modal: false,
            show: 'drop',
            hide: 'drop'
        });

        getConnectedUserNames($("#author"));

        $("#domains").chosen();
        $("#tags").chosen();

        $("#saveBtn").click(function(){
            $("#confirmationDialog").dialog('open');
        });

        var btnUpload=$('#upload');
        var uploadStatus=$('#upstatus');
        var uploadFileInfo = $('#upload-file-info');

        var interval;
        var uploader = null;

        new AjaxUpload(btnUpload, {
            responseType: 'json',
            name: 'file',
            autoSubmit: false, // disable autosubmit, we trigger submit ourself in onChange callback
            action:'',
            multiple: false,
            onChange: function(file, ext) {
                var regexp = new RegExp('^(' + extensions + ')$');

                if (! (ext && regexp.test(ext))){
                    // check for valid file extension
                    uploadStatus.text(extensionsMsgError);
                    return false;
                }
                // on file upload, initialize post process variable.
                postProcess = false;
                uploadFileInfo.val(file);
                uploader = this;

                uploader.setData({extension: uploader.getCurrentFileExtension()});
                uploader.setAction('/upload/' + type);

                uploader.submit(null);
            },
            onSubmit: function(file, ext){
                uploadStatus.text('Transfert en cours ');
                interval = window.setInterval(function(){
                    var text = uploadStatus.text();
                    if (text.length < ('Transfert en cours '.length + 5)){
                        uploadStatus.text(text + '.');
                    } else {
                        uploadStatus.text('Transfert en cours ');
                    }
                }, 200);
            },
            onComplete : function(file, response){
                currentUploadedFileId = response.fileId;
                contentCreator.removeUploadedFiles();
                uploadedFiles.push(response.fileId);
                uploadStatus.text('Transfert terminé');
                contentCreator.refreshSubmitButton();

                var child = createPrevisualizationObject("/content/content/" + currentUploadedFileId);
                $('#previsualizationContainer').empty().append(child);
                window.clearInterval(interval);
            }
        });
    })();
}