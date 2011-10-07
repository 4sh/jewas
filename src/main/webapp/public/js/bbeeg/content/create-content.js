function ContentCreator(type, extensions, extensionsMsgError, previsualizationContainerId, createPrevisualizationObject) {
    var uploadedFiles = [];
    var currentUploadedFileId;

    this.loadDomains = function(domainIds) {
        console.log("domainIds", domainIds);
        $.getJSON(
            '/domain/all',
            function success(data) {
                var container = $("#domains");
                container.children().remove();
                var selectedDomains = {};
                for (var i=0; i < domainIds.length; i++) {
                    selectedDomains[domainIds[i]]=true;
                }
                for (var j=0; j < data.length; j++) {
                    if (selectedDomains[data[j].id]) {
                        data[j].selected = true;
                    } else {
                        data[j].selected = false;
                    }
                }
                $("#domainItemTemplate").tmpl(data).appendTo(container);
                $("#domains").trigger("liszt:updated");
            }
        );
    }

    this.loadTags = function(tags) {
        console.log("selected tags:", tags);
        $.getJSON(
            '/tags/all',
            function success(data) {
                var container = $("#tags");
                container.children().remove();

                var selectedTags = {};

                for (var i = 0; i < tags.length; i++) {
                    selectedTags[tags[i]] = true;
                }
                for (var j = 0; j < data.length; j++) {
                    if (selectedTags[data[j].tag]) {
                        data[j].selected = true;
                    } else {
                        data[j].selected = false;
                    }
                }
                $("#tagItemTemplate").tmpl(data).appendTo(container);
                $("#tags").trigger("liszt:updated");
            }
        );
    }

    function getDomains(domainIds) {
        var domains = [];

        if (domainIds !== null) {
            for (var i=0; i < domainIds.length; i++) {
                domains.push({id: domainIds[i]});
            }
        }
        return domains;
    }


    this.removeUploadedFiles = function () {
        $.delete(
            '/upload',
             {fileNames : JSON.stringify(uploadedFiles)}
        );
    };

    (function () {

        $("#confirmationDialog").dialog({
            autoOpen: false,
            modal: false,
            show: 'drop',
            hide: 'drop'
        });

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

            $.put(form.action,
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

                uploadedFiles.push(response.fileId);

                uploadStatus.text('Transfert terminé');

                var child = createPrevisualizationObject("/content/content/" + currentUploadedFileId);

                $('#previsualizationContainer').empty().append(child);

                window.clearInterval(interval);
            }
        });
    })();
}