function ContentCreator(type, extensions, extensionsMsgError, previsualizationContainerId, createPrevisualizationObject, saveButton, initialContent) {
    var uploadedFiles = [];
    var currentUploadedFileId = "";
    var contentCreator = this;

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
            modal: false,
            show: 'drop',
            hide: 'drop'
        });

        getConnectedUserNames($("#author"));

        $("#domains").chosen();
        $("#tags").chosen();

        $("#createContent").submit(function(){
            var form = this;

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

                    $.post("/content/" + data.id + "/content/" + currentUploadedFileId,
                        null,
                        function () {
                            $("#confirmationDialog").dialog('open');
                            setTimeout(function(){
                                $("#confirmationDialog").dialog('close');
                                window.location.href = "/content/" + data.id + "/view.html";
                            }, 2000);
                        }
                    );


                }
            );

            return false;
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