function EegContentCreator(eegUploaderId, videoUploaderId) {
    var eegUploader;
    var videoUploader;

    function loadDomains() {
        $.getJSON(
            '/domain/all',
            function success(data) {
                var container = $("#domains");
                container.children().remove();
                $("#domainItemTemplate").tmpl(data).appendTo(container);
                $("#domains").trigger("liszt:updated");
            }
        );
    }

    function getDomains(domainIds) {
        var domains = [];

        for (var i=0; i < domainIds.length; i++) {
            domains.push({id: domainIds[i]});
        }

        return domains;
    }


    function createUploader(setUploader, uploaderId, extensions, extensionsMsgError) {
        var btnUpload=$( '#' + uploaderId + ' .upload');
        var uploadStatus=$( '#' + uploaderId + ' .upload-status');
        var interval;

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
                uploadStatus.text(file);
                setUploader(this);
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
                window.clearInterval(interval);
            }
        });
    }

    function getMontages() {
        var montages = [];

        $('.montage').each(
            function (montageIndex, montageElt) {
                var montage = {signalsToDisplay:[], operations: []};

                var signalsToDisplay = $(montageElt).find('.montage-signalsToDisplay')[0].value.split(',');
                montage.signalsToDisplay = signalsToDisplay;

                $(montageElt).find('.montage-operation').each(
                    function (operationIndex, operationElt) {
                        var s1 = $(operationElt).find('.montage-operation-s1')[0].value;
                        var operator = $(operationElt).find('.montage-operation-operator')[0].value;
                        var s2 = $(operationElt).find('.montage-operation-s2')[0].value;

                        var operation = {'s1': s1,
                            'operator': operator,
                            's2': s2
                        };

                        montage.operations.push(operation);
                    }
                );

                montages.push(montage);
            }
        );

console.log("montages", montages);
        return montages;
    }

    function getEegSettings() {
        var settings = {};

        settings.eegStart = $('#eegStart')[0].value;
        settings.eegStop = $('#eegStop')[0].value;
        settings.zoom = $('#zoom')[0].value;
        settings.frameDuration = $('#frameDuration')[0].value;
        settings.montages = getMontages();

        return settings;
    }

    function sendEegSettings(contentId, callAfter) {
        var eegSettings = getEegSettings();

        // Note the text is set as a query parameter because if set in data, then get an exception server side.
        $.put('/content/eeg/settings/' + contentId + '?text='+JSON.stringify(eegSettings),
            null,
            //eegSettings,
            function(data){
                callAfter(contentId);
            }
        );
    }

    function launchUploads(contentId) {
        if (eegUploader != null) {
            eegUploader.setData({extension: eegUploader.getCurrentFileExtension()});
            eegUploader.setAction('/content/content/' + contentId + '/' + 'EEG');

            eegUploader.submit(function () {

                if (videoUploader != null) {
                    videoUploader.setData({extension: videoUploader.getCurrentFileExtension()});
                    videoUploader.setAction('/content/content/' + contentId + '/' + 'EEG');

                    videoUploader.submit(function () {
                        $("#confirmationDialog").dialog('open');
                        setTimeout(function(){
                            $("#confirmationDialog").dialog('close');
                            window.location.href = "/content/" + contentId + "/view.html";
                        }, 2000);
                    });
                }

            });
        }
    }


    (function () {
        loadDomains();

        $("#confirmationDialog").dialog({
            autoOpen: false,
            modal: false,
            show: 'drop',
            hide: 'drop'
        });

        $("#domains").chosen();

        $("#createContent").submit(function(){
            var form = this;

            var contentDetail = {
                header: {
                    title: $("#title").val(),
                    description: $("#description").val(),
                    domains: getDomains($("#domains").val())
                }
            }


            var dataToSend = {
                type : 'EEG',
                contentDetail : JSON.stringify(contentDetail)
            };

            $.put(form.action,
                dataToSend,
                function(data){
                    sendEegSettings(data.id, launchUploads);
                }
            );

            return false;
        });

        eegUploader = null;
        createUploader(function (uploader){eegUploader = uploader;}, eegUploaderId, 'edf', 'Seul le format EDF est accepté.');

        videoUploader = null;
        createUploader(function (uploader){videoUploader = uploader;}, videoUploaderId, 'mp4', 'Seuls le format MP4 est accepté.');
    })();
}