function EegContentCreator(eegUploaderId, previsualizationInfos) {
    /* ***************************************************************************************************************
     *  Private attributes
     *****************************************************************************************************************/
    var eegUploader;
    var videoUploader;
    var eegInformations;

    var signals;

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

    function loadTags(tags) {
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
           // $("#tagItemTemplate").tmpl(data).appendTo(container);
            $("#tags").trigger("liszt:updated");
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

    function loadEegInformations() {
        $.getJSON(
            '/content/eeg/informations/' + eegId,
            function success(data) {
                eegInformations = $.parseJSON(data);

                signals  = [];

                for (var i = 0; i < eegInformations.signalsLabel.length; i++) {
                    signals.push({id: i, label: eegInformations.signalsLabel[i]});
                }

                // TODO: enable eeg settings form

                addVideo($('#videos'));
                addMontage($('#montages'));

                var max = eegInformations.eegDuration / 1000;
                $('#eegStop').keyup(function () {
                    if(Number(this.value) > max) {
                        this.value = max;
                    }
                });
            }
        );
    }

    function createEegUploader(uploaderSetter) {
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
                    // Nothing to do for now
                },
                onCompleteCallback: function (uploader, response) {
                    eegId = response.fileId;
                    loadEegInformations();
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
        var uploadFileInfo = uploaderComponent.find('.upload-file-info');
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
                uploadFileInfo.val(file);
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
                uploadStatus.text('Transfert terminé');

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

                if (!!start && !!stop && (!!videoUploader && !!videoUploader.videoId)) {
                    video.start = start * 1000;
                    video.stop = stop * 1000;
                    // TODO: use the right uploader.
                    video.fileName = videoUploader.videoId;

                    videos.push(video);
                }
            }
        );

        return videos;
    }

    function getMontages() {
        var montages = [];

        $('.montage').each(
            function (montageIndex, montageElt) {
                var montage = {signalsToDisplay:[], operations: []};

                var signalsToDisplay = $(montageElt).find('.montage-signalsToDisplay').val();
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

        settings.eegStart = $('#eegStart')[0].value * 1000;

        if (!settings.eegStart) {
            settings.eegStart = 0;
        }

        settings.eegStop = $('#eegStop')[0].value * 1000;

        if (!settings.eegStop) {
            settings.eegStop = -1;
        }

        settings.zoom = $('#zoom')[0].value;
        settings.frameDuration = $('#frameDuration')[0].value;
        settings.montages = getMontages();

        settings.videos = getVideos();

        return settings;
    }

    function sendEegSettings(contentId, callAfter, mode) {
        var eegSettings = getEegSettings();

        var url = '/content/eeg/settings/'+ contentId;

        if (!mode ||'tmp' === mode) {
            url += '/' + mode;
        }

        // Note the text is set as a query parameter because if set in data, then get an exception server side.
        // TODO: Can be changed now
        $.put(url + '?text='+JSON.stringify(eegSettings),
            null,
            //eegSettings,
            function(data){
                if (!!callAfter) {
                    callAfter(contentId);
                }
            }
        );
    }

    function buildPrevisualization() {
        // Create the iframe to previsualize with the eeg pevisualization url
        var iframe = document.createElement('iframe');

        iframe.width = '100%';
        iframe.height = '100%';

        iframe.src = previsualizationInfos.previsualizationUrl + '/' + eegId + '/previsualization.html';

        $('#' + previsualizationInfos.previsualizationContainerId).empty().append(iframe);
    }

    function previsualizeAction() {
        //Save eeg settings in tmp mode
        sendEegSettings( eegId, buildPrevisualization, 'tmp');
    }

    function addVideo(container) {
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

         videoItem.find('.video-start').keyup(function () {
             var min = $('#eegStart').val();

             if (!!min) {
                 min = 0;
             }

             if(Number(this.value) < min) {
                 this.value = min;
             }
        });

        videoItem.find('.video-stop').keyup(function () {
            var max = $('#eegStop').val();

            if(Number(this.value) > max) {
                this.value = max;
            }
        });
    };

    function addMontage(container) {
        var montage = $("#montageItemTemplate").tmpl(null);
        montage.appendTo(container);

        $("#signalItemTemplate").tmpl(signals).appendTo(montage.find('.montage-signalsToDisplay'));

        montage.find('.montage-signalsToDisplay').chosen();

        addMontageOperation(montage);
    };

    function deleteMontage(element) {
        $(element).remove();
    };

    function addMontageOperation(container) {
        var montageOperation = $("#montageOperationItemTemplate").tmpl(null);
        montageOperation.appendTo(container);

        montageOperation.find('.montage-operation-delete').bind('click', function () { deleteMontageOperation(montageOperation)});
        montageOperation.find('.montage-operation-add').bind('click', function () { addMontageOperation(container)});

         $("#signalItemTemplate").tmpl(signals).appendTo(montageOperation.find('.montage-operation-s1'));
         $("#signalItemTemplate").tmpl(signals).appendTo(montageOperation.find('.montage-operation-s2'));

        montageOperation.find('.montage-operation-s1').chosen();
        montageOperation.find('.montage-operation-s2').chosen();
    };

    function deleteMontageOperation(element) {
        $(element).remove();
    };

    /* ***************************************************************************************************************
     *  Public methods
     *****************************************************************************************************************/

    this.removeUploadedFiles = function () {
        $.delete(
            '/upload/eeg/' + eegId
        );
    };

    /* ***************************************************************************************************************
     *  Constructor
     *****************************************************************************************************************/

    (function () {
        loadDomains();
        loadTags([]) ;

        $("#confirmationDialog").dialog({
            autoOpen: false,
            modal: false,
            show: 'drop',
            hide: 'drop'
        });

        $("#domains").chosen();
        $("#tags").chosen();
        $("#zoom").chosen();
        $("#frameDuration").chosen();



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

        $('#' + previsualizationInfos.previsualizeButtonId).bind('click', previsualizeAction);
    })();
}