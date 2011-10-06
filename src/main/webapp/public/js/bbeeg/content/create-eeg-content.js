function EegContentCreator(eegUploaderId, videoUploaderId) {
    /* ***************************************************************************************************************
     *  Private attributes
     *****************************************************************************************************************/
    var eegUploader;
    var videoUploader;

    var eegId;

    /* ***************************************************************************************************************
     *  Private methods
     *****************************************************************************************************************/

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

    function createEegUploader(uploaderSetter) {
        // Create uploader
        // onChange: clean eegsettings, disabled buttons, send request to rename uploaded eeg and videos...
        // onSubmit: enabled buttons
        // TODO

        createUploader(
            uploaderSetter,
            $('#' + eegUploaderId),
            {
                extensions: 'edf',
                extensionsMsgError: 'Seul le format EDF est accepté.',
                onChangeCallback: function (uploader) {
                    uploader.setData({extension: eegUploader.getCurrentFileExtension()});
                    uploader.setAction('/content/content/EEG');
                    uploader.submit();
                },
                onSubmitCallback: function (uploader) {
// Nothing to do
                },
                onCompleteCallback: function (uploader, response) {
                    eegId = response.fileId;
// TODO: enable eeg settings form
                }
            }
        );
    }

    /*
     * options an object that can contains these properties :
     * - extensions
     * - extensionsMsgError
     * - onChangeCallback
     * - onSubmitCallback
     * - onCompleteCallback
     */
    function createUploader(uploaderSetter, uploaderComponent, options) {
        var btnUpload = uploaderComponent.find('.upload');
        var uploadStatus = uploaderComponent.find('.upload-status');
        var interval;

        new AjaxUpload(btnUpload, {
            responseType: 'json',
            name: 'file',
            autoSubmit: false, // disable autosubmit, we trigger submit ourself in onChange callback
            action:'',
            multiple: false,
            onChange: function(file, ext) {
                var regexp = null;

                if (!!options.extensions) {
                    regexp = new RegExp('^(' + options.extensions + ')$');
                }

                if (regexp !== null && !(ext && regexp.test(ext))){
                    // check for valid file extension
                    var extensionsMsgError = "Extensions not supported";

                    if (!!options.extensionsMsgError) {
                        extensionsMsgError = options.extensionsMsgError;
                    }
                    
                    uploadStatus.text(extensionsMsgError);
                    return false;
                }
                uploadStatus.text(file);
                uploaderSetter(this);

                if (!!options.onChangeCallback) {
                    options.onChangeCallback(this);
                }
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

                if (!!options.onSubmitCallback) {
                    options.onSubmitCallback(this);
                }

            },
            onComplete : function(file, response){
                window.clearInterval(interval);

                if (!!options.onCompleteCallback) {
                    options.onCompleteCallback(this, response);
                }
            }
        });
    }

    function getVideos() {
        var videos = [];

        $('.video-uploader').each(
            function (videoIndex, videoElt) {
                var video = {};

                var start = $(videoElt).find('.video-start').val();
                var stop = $(videoElt).find('.video-stop').val();

                // TODO add controls

                video.start = start;
                video.stop = stop;
                // TODO: use the right uploader.
                video.fileName = videoUploader.videoId;

                videos.push(video);
            }
        );

        return videos;
    }

    function getMontages() {
        var montages = [];

        $('.montage').each(
            function (montageIndex, montageElt) {
                var montage = {signalsToDisplay:[], operations: []};

                var signalsToDisplay = $(montageElt).find('.montage-signalsToDisplay')[0].value.split(',');
                if (!!signalsToDisplay) {
                    montage.signalsToDisplay = signalsToDisplay;
                }

                $(montageElt).find('.montage-operation').each(
                    function (operationIndex, operationElt) {
                        var s1 = $(operationElt).find('.montage-operation-s1')[0].value;
                        var operator = $(operationElt).find('.montage-operation-operator')[0].value;
                        var s2 = $(operationElt).find('.montage-operation-s2')[0].value;

                        if (!!s1 && !!operator && !!s2) {
                            var operation = {'s1': s1,
                                'operator': operator,
                                's2': s2
                            };

                            montage.operations.push(operation);
                        }
                    }
                );

                if (montage.signalsToDisplay.length !== 0 && montage.operations.length !== 0) {
                    montages.push(montage);
                }
            }
        );

        return montages;
    }

    function getEegSettings() {
        var settings = {};

        settings.eegStart = $('#eegStart')[0].value;

        if (!settings.eegStart) {
            settings.eegStart = 0;
        }

        settings.eegStop = $('#eegStop')[0].value;

        if (!settings.eegStop) {
            settings.eegStop = -1;
        }

        settings.zoom = $('#zoom')[0].value;
        settings.frameDuration = $('#frameDuration')[0].value;
        settings.montages = getMontages();

        settings.videos = getVideos();

        return settings;
    }

    function sendEegSettings(contentId, callAfter) {
        var eegSettings = getEegSettings();

        // Note the text is set as a query parameter because if set in data, then get an exception server side.
        $.put('/content/eeg/settings/' + contentId + '?text='+JSON.stringify(eegSettings),
            null,
            //eegSettings,
            function(data){
                if (!!callAfter) {
                    callAfter(contentId);
                }
            }
        );
    }

//    function launchUploads(contentId) {
//        if (eegUploader != null) {
//            eegUploader.setData({extension: eegUploader.getCurrentFileExtension()});
//            eegUploader.setAction('/content/content/' + contentId + '/' + 'EEG');
//
//            eegUploader.submit(function () {
//
//                if (videoUploader != null) {
//                    videoUploader.setData({extension: videoUploader.getCurrentFileExtension()});
//                    videoUploader.setAction('/content/content/' + contentId + '/' + 'EEG');
//
//                    videoUploader.submit(function () {
//                        $("#confirmationDialog").dialog('open');
//                        setTimeout(function(){
//                            $("#confirmationDialog").dialog('close');
//                            window.location.href = "/content/" + contentId + "/view.html";
//                        }, 2000);
//                    });
//                }
//
//            });
//        }
//    }

    /* ***************************************************************************************************************
     *  Public methods
     *****************************************************************************************************************/

    this.addVideo = function (container) {
        var videoItem = $("#videoItemTemplate").tmpl();

         videoItem.appendTo(container);

        createUploader(
            function (uploader) {
                videoUploader = uploader;
            },
            videoItem,
            {
                extensions: 'mp4',
                extensionsMsgError: 'Seul le format MP4 est accepté.',
                onChangeCallback: function (uploader) {
                    uploader.setData({extension: videoUploader.getCurrentFileExtension()});
                    uploader.setAction('/content/content/EEG/' + eegId);
                    uploader.submit();
                },
                onCompleteCallback: function (uploader, response) {
                    uploader.videoId = response.fileId;
                }
            }
        );


    };

    this.addMontage = function (container) {
        var montage = $("#montageItemTemplate").tmpl(null);
        montage.appendTo(container);
        this.addMontageOperation(montage);
    };

    this.deleteMontage = function (element) {
        $(element).remove();
    };

    this.addMontageOperation = function (container) {
        var montageOperation = $("#montageOperationItemTemplate").tmpl(null);

        var eegContentCreator = this;
        montageOperation.find('.montage-operation-delete').bind('click', function () { eegContentCreator.deleteMontageOperation(montageOperation)});
        montageOperation.find('.montage-operation-add').bind('click', function () { eegContentCreator.addMontageOperation(container)});

        montageOperation.appendTo(container);
    };

    this.deleteMontageOperation = function (element) {
        $(element).remove();
    };

    /* ***************************************************************************************************************
     *  Constructor
     *****************************************************************************************************************/

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
                    var contentId = data.id;
                    sendEegSettings(
                        contentId,
                        function () {
                            $.put("/content/eeg/" + eegId + "/" + contentId,
                                null,
                                function () {
                                    $("#confirmationDialog").dialog('open');
                                    setTimeout(function(){
                                        $("#confirmationDialog").dialog('close');
                                        window.location.href = "/content/" + contentId + "/view.html";
                                    }, 2000);
                                }
                            );
                        }
                    );
                }
            );

            return false;
        });

        eegUploader = null;
        createEegUploader(
            function (uploader){
                eegUploader = uploader;
            }
        );

        videoUploader = null;
//        createUploader(
//            function (uploader){
//                videoUploader = uploader;
//            },
//            $('#'+videoUploaderId),
//            {
//                extensions: 'mp4',
//                extensionsMsgError: 'Seuls le format MP4 est accepté.',
//                onChangeCallback: function (uploader) {
//                    uploader.setData({extension: videoUploader.getCurrentFileExtension()});
//                    uploader.setAction('/content/content/EEG/' + eegId);
//                    uploader.submit();
//                }
//            }
//        );
    })();
}