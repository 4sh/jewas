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

    function checkStartStopTimeConsistency(startTime, stopTime) {
        if (stopTime < startTime) {
            return false;
        } else if (startTime === stopTime && startTime !== 0) {
            // A value is specified and this this the same for both start and stop
            return false;
        }
        // If no value specified, start and stop equals to 0 => Not an error beacause 
        // there are default behavior for that particular case.
        return true;
    }

    function addErrorMessage(message, targetContainer) {
        $('#errorMessageTemplate').tmpl({errorMessage: message}).appendTo(targetContainer);
    }

     function cleanErrorMessage(targetContainer) {
        targetContainer.children().remove();
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

                /* Initialize max eeg duration input fields */
                setTimeFromMilliSeconds(eegInformations.eegDuration, $('#eegStopHours')[0], $('#eegStopMinutes')[0], $('#eegStopSeconds')[0]);

                addVideo($('#videos'));
                addMontage($('#displayConfig'));

                var max = eegInformations.eegDuration;

                // Key up listeners registration on time configuration inputs
                $('#eegStartHours, #eegStartMinutes, #eegStartSeconds, #eegStopHours, #eegStopMinutes, #eegStopSeconds').keyup(function () {
                    cleanErrorMessage($('.eegErrorBanner'));
                    var eegStart = getTimeInMilliSeconds($('#eegStartHours').val(),
                                                             $('#eegStartMinutes').val(),
                                                             $('#eegStartSeconds').val());
                    var eegStop = getTimeInMilliSeconds($('#eegStopHours').val(),
                                                            $('#eegStopMinutes').val(),
                                                            $('#eegStopSeconds').val());
                    if (!checkStartStopTimeConsistency(eegStart, eegStop)) {
                        var message = "Current stop time is greater than current start time";
                        addErrorMessage(message,$('.eegErrorBanner'));
                    }
                });

                $('.video-start-hours, .video-start-minutes, .video-start-seconds, .video-stop-hours, .video-stop-minutes, .video-stop-seconds').keyup(function () {
                    cleanErrorMessage($('.video-conf-error-banner'));
                    var currentStart = getTimeInMilliSeconds($('.video-start-hours').val(),
                                                             $('.video-start-minutes').val(),
                                                             $('.video-start-seconds').val());

                    var currentStop = getTimeInMilliSeconds($('.video-stop-hours').val(),
                                                            $('.video-stop-minutes').val(),
                                                            $('.video-stop-seconds').val());
                    if (!checkStartStopTimeConsistency(currentStart, currentStop)) {
                        var message = "Current stop time is greater than current start time";
                        addErrorMessage(message, $('.video-conf-error-banner'));
                    }
                });

                $('.video-stop-hours, .video-stop-minutes, .video-stop-seconds').keyup(function () {
                    cleanErrorMessage($('.video-conf-error-banner'));
                    var currentStop = getTimeInMilliSeconds($('.video-stop-hours').val(),
                                                            $('.video-stop-minutes').val(),
                                                            $('.video-stop-seconds').val());

                    console.log("Max: ", max);
                    console.log("Current:", currentStop);
                    if (currentStop > max) {
                        var message = "Current stop time is greater than eeg max length: " + max / 1000 + "seconds";
                        addErrorMessage(message, $('.video-conf-error-banner'));
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

    function setTimeFromMilliSeconds(timeInMillis, hourInput, minuteInput, secondInput) {
        if (timeInMillis == null) {
            console.error("Time in millis", timeInMillis);
            return;
        }
        if (!hourInput || !minuteInput || !secondInput) {
            console.error("Missing input", hourInput, minuteInput, secondInput);
            return;
        }
        var hours =  Math.round(timeInMillis / 3600000);
        timeInMillis -= hours * 3600000;
        var minutes = Math.round(timeInMillis / 60000);
        timeInMillis -= minutes * 60000;
        var seconds = Math.round(timeInMillis /1000);
        hourInput.value = hours;
        minuteInput.value = minutes;
        secondInput.value = seconds;
    }

    function getTimeInMilliSeconds(hours, minutes, seconds) {
        var time = 0;
        if (!!hours) {
            time += Number(hours) * 3600000;
        }
        if (!!minutes) {
            time += Number(minutes) * 60000;
        }
        if (!!seconds) {
            time += Number(seconds) * 1000;
        }
        console.log("getTimeInSeconds(hours:" + hours + ",minutes:" + minutes +",seconds:" + seconds + ")=>time: ", time);
        return time;
    }

    function getVideos() {
        var videos = [];

        $('.video').each(
            function (videoIndex, videoElt) {
                var video = {};

                var startHours = $(videoElt).find('.video-start-hours').val();
                var startMinutes = $(videoElt).find('.video-start-minutes').val();
                var startSeconds = $(videoElt).find('.video-start-seconds').val();

                var stopHours = $(videoElt).find('.video-stop-hours').val();
                var stopMinutes = $(videoElt).find('.video-stop-minutes').val();
                var stopSeconds = $(videoElt).find('.video-stop-seconds').val();

                var start = getTimeInMilliSeconds(startHours, startMinutes, startSeconds);
                var stop = getTimeInMilliSeconds(stopHours, stopMinutes, stopSeconds);

                if (start != null && stop != null && (!!videoUploader && !!videoUploader.videoId)) {
                    video.start = start;
                    video.stop = stop;
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

                if (montage.signalsToDisplay.length !== 0) {
                    montages.push(montage);
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
            }
        );

        return montages;
    }

    function buildEegSettings() {
        var settings = {};

        settings.eegStart = getTimeInMilliSeconds($('#eegStartHours').val(), $('#eegStartMinutes').val(), $('#eegStartSeconds').val());
        settings.eegStop = getTimeInMilliSeconds($('#eegStopHours').val(), $('#eegStopMinutes').val(), $('#eegStopSeconds').val());

        if (settings.eegStop === 0) {
            settings.eegStop = -1;
        }

        settings.zoom = $('#zoom')[0].value;
        settings.frameDuration = $('#frameDuration')[0].value;
        settings.montages = getMontages();
        settings.videos = getVideos();

        return settings;
    }

    function sendEegSettings(contentId, callAfter, mode) {
        var eegSettings = buildEegSettings();
        console.log(JSON.stringify(eegSettings));
        var url = '/content/eeg/settings/'+ contentId;

        if (!mode ||'tmp' === mode) {
            url += '/' + mode;
        }

        $.ajaxPut(url + "?text=" + JSON.stringify(eegSettings),
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

        iframe.width = '1040px';
        iframe.height = '800px';
        iframe.frameBorder = '0';

        iframe.src = previsualizationInfos.previsualizationUrl + '/' + eegId + '/previsualization.html';

        $('#' + previsualizationInfos.previsualizationContainerId).empty().append(iframe);
    }

    function previsualizeAction() {
        sendEegSettings(eegId, buildPrevisualization, 'tmp');
    }

    function addVideo(container) {
        var videoItem = $("#videoItemTemplate").tmpl();
        $(container).empty();
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
                    setTimeFromMilliSeconds(eegInformations.eegDuration, $('.video-stop-hours')[0], $('.video-stop-minutes')[0], $('.video-stop-seconds')[0]);
                    sendEegSettings(eegId, null, 'tmp');
                }
            }
        );
    };

    function addMontage(container) {
        var montage = $("#montageItemTemplate").tmpl(null);
        $(container).empty();
        montage.appendTo(container);

        $("#signalItemTemplate").tmpl(signals).appendTo(montage.find('.montage-signalsToDisplay'));

        montage.find('.montage-signalsToDisplay').chosen();

        addMontageOperation($('#montages .montage'));
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
        $.ajaxDelete(
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

        $('#allSignals').live('click', (function() {
           if ($(this).attr('checked')) {
               signals = [];
               $('.montage-signalsToDisplay').attr('disabled', 'true');
               $('.montage-signalsToDisplay').val([]);
               $('.montage-signalsToDisplay').trigger("liszt:updated");
           } else {
               $('.montage-signalsToDisplay').removeAttr('disabled');
               $('.montage-signalsToDisplay').trigger("liszt:updated");
           }
        }));

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
                type : 'EEG',
                contentDetail : JSON.stringify(contentDetail)
            };

            $.ajaxPut(form.action,
                dataToSend,
                function(data){
                    var contentId = data.id;
                    sendEegSettings(
                        eegId,
                        function () {
                            $.ajaxPut("/content/eeg/" + eegId + "/" + contentId,
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