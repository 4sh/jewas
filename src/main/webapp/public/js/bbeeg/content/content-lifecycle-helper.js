/**
 * Content life cycle helper provides methods to manage contents status updates.
 */
var contentLifeCycleHelper = (function() {

    /*
     * Private methods
     */

    /**
     *
     *
     * @param containerId
     * @param contentId
     * @param updateStatusData
     */
    function sendUpdateStatus(containerId, contentId, updateStatusData) {
        $.post('/content/status/' + contentId,
            updateStatusData,
            function () {
                openInformationDialog("updateStatusSuccessDialog");
                var statusStyle = contentHelper.getStatusStyle(updateStatusData.newStatus());
                $('#' + containerId).find('.tab_right').removeClass().addClass('tab_right ' + statusStyle.className);
                $('#' + containerId).find('.label').html(statusStyle.label);
                refreshActionButtons(containerId, updateStatusData.newStatus());
                switch (updateStatusData.newStatus()) {
                    case ContentStatus.DELETED:
                        $("#" + containerId).remove();
                        break;
                }
            }
        );
    }

    function refreshActionButtons(containerId, contentStatus) {
        $("#" + containerId + ' .edit-button').css("visibility",
            contentLifeCycleHelper.isEditAuthorized(contentStatus) ? "visible" : "hidden");
        $("#" + containerId + ' .publish-button').css("visibility",
            contentLifeCycleHelper.isPublishAuthorized(contentStatus) ? "visible" : "hidden");
        $("#" + containerId + ' .delete-button').css("visibility",
            contentLifeCycleHelper.isDeleteAuthorized(contentStatus) ? "visible" : "hidden");
        $("#" + containerId + ' .accept-button').css("visibility",
            contentLifeCycleHelper.isAcceptAuthorized(contentStatus) ? "visible" : "hidden");
        $("#" + containerId + ' .reject-button').css("visibility",
            contentLifeCycleHelper.isRejectAuthorized(contentStatus) ? "visible" : "hidden");
        $("#" + containerId + ' .display-publication-comments-button').css("visibility",
            contentLifeCycleHelper.isDisplayPublicationRequestCommentsAuthorized(contentStatus) ? "visible" : "hidden");
        $("#" + containerId + ' .display-rejection-comments-button').css("visibility",
            contentLifeCycleHelper.isDisplayRejectionCommentsAuthorized(contentStatus) ? "visible" : "hidden");
    }

    /**
     * Loads the content of the publication dialog or rejection dialog depending on the given action.
     *
     * @param dialogId the dialog identifier
     * @param action the action on which the dialog must display information (can be 'publication' or 'rejection')
     * @param contentInfo the content information (id and current status)
     */
    function loadPublicationRejectionDialogContent(dialogId, action, contentInfo) {
        var dialog = $("#" + dialogId);
        dialog.children().remove();

        var templateParams = {
            contentStatus: contentInfo.status,
            title: action === "publication" ? "Demande de publication :" : "Motif de rejet :"
        };

        $("#publicationRejectionTemplate").tmpl(templateParams).appendTo(dialog);

        var textArea = dialog.find("textarea");
        var startDate = dialog.find("#startPublicationDate");
        var endDate = dialog.find("#endPublicationDate");

        $.getJSON("/content/status/" + contentInfo.id,
            function success(data) {

                if (!!action) {
                    if (action === "rejection") {
                        textArea.val(data.rejectionComments);
                    } else if (action === "publication") {
                        textArea.val(data.publicationComments);
                    } else {
                        console.error("Cannot guess comments type for loading", action);
                    }
                }
                if (!!data.header.startPublicationDate) {
                    startDate.text(data.header.startPublicationDate);
                } else {
                    startDate.text("");
                }
                if (!!data.header.endPublicationDate) {
                    endDate.text(data.header.endPublicationDate);
                } else {
                    endDate.text("");
                }
            });
    }

    /**
     * Setup the publication / rejection dialog date picker.
     */
    function setupPublicationDatePicker() {
        $("#startPublicationDate").datepicker("setDate", new Date());
        var dates = $("#startPublicationDate, #endPublicationDate").datepicker({
            defaultDate: null,
            dateFormat: bbeegConfig.localeDateFormat(),
            changeMonth: true,
            numberOfMonths: 1,
            minDate: new Date(),
            onSelect: function(selectedDate) {
                var option = (this.id == "startPublicationDate") ? "minDate" : "maxDate",
                    instance = $(this).data("datepicker"),
                    date = $.datepicker.parseDate(
                        instance.settings.dateFormat || $.datepicker._defaults.dateFormat,
                        selectedDate,
                        instance.settings);
                dates.not(this).datepicker("option", option, date);
            }
        });
    }

    /**
     * Opens a read only dialog that displays informations on the content.
     *
     * @param dialogId the identifier of the dialog to open.
     */
    function openInformationDialog(dialogId, contentLoadFunction) {
        if (!!contentLoadFunction) {
            contentLoadFunction();
        }
        var dialog = $("#" + dialogId);
        dialog.dialog(
            {   show: 'slide',
                hide: 'slide',
                width: '40%',
                buttons: [
                    {
                        text: "Ok",
                        click: function() {
                            $(this).dialog("close");
                        }
                    }
                ]
            });
    }

    /**
     * Opens a dialog on content action.
     *
     * @param dialogId the identifier of the dialog to open
     * @param options the dialog options (validateActionMethod, cancelActionMethod to call). If null, default behavior is applied
     */
    function openActionDialog(dialogId, options) {
        var defaultOptions = {
            dialogMessage: "",
            validateAction: function() {
                
            },
            cancelAction: function() {

            }
        };
        var _options = options || defaultOptions;

        var dialog = $("#" + dialogId);

        dialog.find(".dialogMessage").html(_options.dialogMessage);
        dialog.dialog(
            {   show: 'slide',
                hide: 'slide',
                width: '40%',
                buttons: [
                    {
                        text: "Valider",
                        click: function() {
                            _options.validateAction();
                            $(this).dialog("close");
                        }
                    },
                    {
                        text: "Annuler",
                        click: function() {
                            options.cancelAction();
                            $(this).dialog("close");
                        }
                    }
                ]
            }
        );
    }

    function rejectActionConfirmation(containerId, contentId, status, comment) {
        var newStatus = null;

        if (status === ContentStatus.TO_BE_VALIDATED) {
            newStatus = ContentStatus.REJECTED;
        } else {
            if (status === ContentStatus.TO_BE_DELETED) {
                newStatus = ContentStatus.VALIDATED;
            }
            else {
                openInformationDialog("updateStatusImpossible");
            }
        }

        if (newStatus !== null) {
            sendUpdateStatus(containerId, contentId, new UpdateStatusData().newStatus(newStatus).comments(comment));
        }
    }

    /*
     * Public methods
     */
    return {

        isRejectAuthorized: function(contentStatus) {
             return (contentStatus === ContentStatus.TO_BE_VALIDATED || contentStatus === ContentStatus.TO_BE_DELETED);
        },

        isAcceptAuthorized:function(contentStatus) {
            return (contentStatus === ContentStatus.TO_BE_VALIDATED || contentStatus === ContentStatus.TO_BE_DELETED);
        },

        isEditAuthorized: function(contentStatus) {
            return (contentStatus === ContentStatus.DRAFT
                 || contentStatus === ContentStatus.REJECTED
                 || contentStatus === ContentStatus.TO_BE_VALIDATED
                 || contentStatus === ContentStatus.VALIDATED);
        },

        isPublishAuthorized: function(contentStatus) {
            return contentStatus === ContentStatus.DRAFT;
        },

        isDeleteAuthorized: function(contentStatus) {
            return contentStatus === ContentStatus.DRAFT
                || contentStatus === ContentStatus.TO_BE_VALIDATED
                || contentStatus === ContentStatus.VALIDATED
                || contentStatus === ContentStatus.REJECTED;
        },

        isDisplayPublicationRequestCommentsAuthorized : function(contentStatus) {
            return contentStatus === ContentStatus.TO_BE_VALIDATED;
        },

        isDisplayRejectionCommentsAuthorized: function(contentStatus) {
            return contentStatus === ContentStatus.REJECTED;
        },

        /**
         * Request for content publication.
         *
         * @param containerId the search result container identifier on which the publication has been required.
         * @param contentId the content identifier to publish
         * @param status the current status of the content to publish.
         */
        publishAction: function(containerId, contentId, status) {
            var newStatus = null;

            if (status === ContentStatus.DRAFT) {
                newStatus = ContentStatus.TO_BE_VALIDATED;
            } else {
                $("#updateStatusImpossible").dialog({show: 'slide', hide: 'slide'});
            }

            if (newStatus !== null) {
                // Do not use openActonDialog here because of a bug on the date picker display
                // Should be refactored
                var publicationDialog = $("#publishContentDialog");
                publicationDialog.children().remove();
                $("#publicationRejectionTemplate").tmpl({contentStatus : status, title: "Demande de publication : "}).appendTo(publicationDialog);

                publicationDialog.dialog(
                    {
                        show: 'slide',
                        open: function () {
                            // For the date picker not to be displayed behind the dialog
                            var dialogZindex = $(this).parents(".ui-dialog").css("z-index");
                            $("#ui-datepicker-div").css("z-index", dialogZindex + 12 + "!Important");
                        },
                        hide: 'slide',
                        width: '50%',
                        buttons: [
                            {
                                text: "Valider",
                                click: function() {
                                    var publicationDialog = $("#publishContentDialog");

                                    var updateStatusData = new UpdateStatusData()
                                        .newStatus(newStatus)
                                        .comments(publicationDialog.find("textarea").val())
                                        .startPublicationDate(publicationDialog.find("#startPublicationDate").val())
                                        .endPublicationDate(publicationDialog.find("#endPublicationDate").val());
                                    sendUpdateStatus(containerId, contentId, updateStatusData);
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
                 setupPublicationDatePicker();
            }
        },

        /**
         * Edit action. Redirect to the GetEditContentRoute which performs actions depending on the content type.
         *
         * @param contentId the identifier of the content to edit.
         */
        editAction: function(contentId) {
            window.location = "/content/" + contentId + "/edit.html";
        },

        /**
         * Request for content deletion. This action is only authorized for content owner.
         *
         * @param containerId
         * @param contentId the identifier of the content to delete
         * @param status the current content status
         */
        deleteAction: function(containerId, contentId, status) {
            if (this.isDeleteAuthorized(status)) {
                var dialogMessage = "<b>Etes-vous sûr de vouloir supprimer ce contenu ?</b></br>";
                if (status === ContentStatus.VALIDATED) {
                    dialogMessage += "Ce contenu est valide, la demande de suppression devra être validée avant d'être effective.";
                } else if (status === ContentStatus.DRAFT || status === ContentStatus.REJECTED) {
                    dialogMessage += "Après validation, le contenu ne sera plus disponible.";
                } else if (status === ContentStatus.TO_BE_VALIDATED) {
                    dialogMessage += "Après validation, le contenu sera retiré de la liste d'attente des contenus à valider et ne sera plus disponible.";
                }

                var dialogOptions = {
                    dialogMessage: dialogMessage,
                    validateAction: function() {
                        var newStatus = null;
                        if (status === ContentStatus.VALIDATED) {
                            newStatus = ContentStatus.TO_BE_DELETED;
                        } else {
                            newStatus = ContentStatus.DELETED;
                        }
                        if (newStatus !== null) {
                            sendUpdateStatus(containerId, contentId, new UpdateStatusData().newStatus(newStatus));
                        }
                    },
                    cancelAction: function() {
                        
                    }
                };
                openActionDialog("acceptActionDialog", dialogOptions);
            } else {
                console.error("Invalid deletion of content:", contentId);
            }
        },

        acceptAction:function (containerId, contentId, status) {
            if (this.isAcceptAuthorized(status)) {
                var dialogMessage = "<b>Etes-vous sûr de vouloir valider ce contenu ?</b></br>";
                if (status === ContentStatus.TO_BE_VALIDATED) {
                    dialogMessage += "Après validation, ce contenu sera consultable par l'ensemble des utilisateurs de la plateforme dans la limite définie par ses dates de publication.";
                } else if (status === ContentStatus.TO_BE_DELETED) {
                    dialogMessage += "Après validation, le contenu ne sera plus disponible sur la plateforme.";
                }
                var dialogOptions = {
                    dialogMessage:dialogMessage,
                    validateAction:function () {
                        var newStatus = null;

                        if (status === ContentStatus.TO_BE_VALIDATED) {
                            newStatus = ContentStatus.VALIDATED;
                        } else {
                            if (status === ContentStatus.TO_BE_DELETED) {
                                newStatus = ContentStatus.DELETED;
                            }
                            else {
                                $("#updateStatusImpossible").dialog({show:'slide', hide:'slide'});
                            }
                        }

                        if (newStatus !== null) {
                            sendUpdateStatus(containerId, contentId, new UpdateStatusData().newStatus(newStatus));
                        }
                    },
                    cancelAction:function () {

                    }
                };
                openActionDialog("deleteActionDialog", dialogOptions);
            } else {
                console.error("Invalid validation of content:", contentId);
            }
        },

        rejectAction: function(searchResultContainerId, contentId, status) {
            var dialogOptions = {
                validateAction: function() {
                    rejectActionConfirmation(searchResultContainerId, contentId, status, $("#rejectReasonDialog > textarea").val());
                },
                cancelAction: function() {

                }
            };
            
            var contentInfo = {
                id: contentId,
                status: status
            };
            loadPublicationRejectionDialogContent("rejectReasonDialog", "rejection", contentInfo);
            openActionDialog("rejectReasonDialog", dialogOptions);
        },

        /**
         * Displays the publication dialog in read-only mode so that the moderator could see why the content owner has
         * made a request for publication.
         *
         * @param contentId the identifier of the current content.
         */
        displayPublicationDialog: function(contentId, contentStatus) {
            openInformationDialog("publishContentDialog", function () {
                var contentInfo = {
                    id: contentId,
                    status: contentStatus
                };
                loadPublicationRejectionDialogContent("publishContentDialog", "publication", contentInfo);
            });
        },

        /**
         * Displays the rejection dialog in read-only mode so that the content owner could see why its submitted content has been rejected.
         *
         * @param contentId the identifier of the current content.
         */
        displayRejectionDialog: function(contentId, contentStatus) {
            openInformationDialog("rejectReasonDialog", function () {
                var contentInfo = {
                    id: contentId,
                    status: contentStatus
                };
                loadPublicationRejectionDialogContent("rejectReasonDialog", "rejection", contentInfo);
            });
        }

    }
})();

/**
 * A class that helps to store information sent back to the server on content stats update.
 */
function UpdateStatusData() {
    var _newStatus;
    var _comments;
    var _startPublicationDate;
    var _endPublicationDate;

    this.newStatus = function(__newStatus) {
        if (__newStatus == null) {
            return _newStatus;
        } else {
            _newStatus = __newStatus;
            return this;
        }
    };

    this.comments = function(__comments) {
        if (__comments == null) {
            return _comments;
        } else {
            _comments = __comments;
            return this;
        }
    };

    this.startPublicationDate = function(__startPublicationDate) {
        if (__startPublicationDate == null) {
            return _startPublicationDate;
        } else {
            _startPublicationDate = __startPublicationDate;
            return this;
        }
    };

    this.endPublicationDate = function(__endPublicationDate) {
        if (__endPublicationDate == null) {
            return _endPublicationDate;
        } else {
            _endPublicationDate = __endPublicationDate;
            return this;
        }
    };
}