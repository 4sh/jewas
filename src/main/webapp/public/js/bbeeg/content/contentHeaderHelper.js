var contentHeaderHelper = (function() {

    return {
        getIcon : function(type) {
            var className;
            switch (type) {
                case 'TEXT':
                    className = "icon_type_text";
                    break;
                case 'IMAGE':
                   className = "icon_type_image";
                    break;
                case 'AUDIO':
                    className = "icon_type_audio";
                    break;
                case 'VIDEO':
                    className = "icon_type_video";
                    break;
                case 'DOCUMENT':
                    className = "icon_type_document";
                    break;
                case 'EEG':
                    className = "icon_type_eeg";
                    break;
                default:
                    className = "";
            }
            return className;
        },

        getStatusStyle : function (status) {
            var statusStyle = {};

            switch (status) {
                case 'VALIDATED':
                    statusStyle.label = "Validé";
                    statusStyle.className = "label_validated";
                    break;
                case 'TO_BE_VALIDATED':
                    statusStyle.label = "A&nbsp;Valider";
                    statusStyle.className = "label_to_validated";
                    break;
                case 'TO_BE_DELETED':
                    statusStyle.label = "A&nbsp;Supprimer";
                    statusStyle.className = "label_to_deleted"
                    break;
                case 'REJECTED':
                    statusStyle.label = "Rejeté";
                    statusStyle.className = "label_rejected";
                    break;
                case 'DRAFT':
                    statusStyle.label = "Brouillon";
                    statusStyle.className = "label_draft";
                    break;
                default:
                    statusStyle.label = "Error";
                    statusStyle.className = "label_hidden";
            }
            return statusStyle;
        }
    };
})();
