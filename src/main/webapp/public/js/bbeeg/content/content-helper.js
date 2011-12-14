var contentHelper = (function() {

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

        getMiniIcon : function(type) {
            var className = this.getIcon(type);
                if (className !== "") {
                    return "mini_" + className;
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
        },

        /**
         * Loads all available domains and display it taking into account the 'selected' property.
         * @param container the chosen container into which the list of domains is loaded
         * @param domainItemTemplate the template used to represent a domain
         * @param selectedDomainIds the domains which are already selected
         */
        loadDomains : function(container, domainItemTemplate, selectedDomainIds) {
            console.log("loadDomains() arg: container", container);
            console.log("loadDomains() arg: domain item template", domainItemTemplate);
            console.log("loadDomains() arg: selectedDomainIds", selectedDomainIds);
            if (!container || !domainItemTemplate || !selectedDomainIds) {
                console.error("At least one loadDomains() argument is undefined")
                return;
            }
            $.getJSON(
                '/domain/all',
                function success(data) {
                    container.children().remove();
                    var selection = {};
                    for (var i = 0; i < selectedDomainIds.length; i++) {
                        selection[selectedDomainIds[i]] = true;
                    }
                    for (var j = 0; j < data.length; j++) {
                        if (selection[data[j].id]) {
                            data[j].selected = true;
                        } else {
                            data[j].selected = false;
                        }
                    }
                    domainItemTemplate.tmpl(data).appendTo(container);
                    container.trigger("liszt:updated");
                }
            );
        },

        /**
         * Loads all available tags and display it taking into account the 'selected' property.
         * @param container the chosen container into which the list of tags is loaded
         * @param tagItemTemplate the template used to represent a tag
         * @param selectedTags the tags which are already selected
         */
        loadTags : function(container, tagItemTemplate, selectedTags) {
            console.log("loadTags() arg: container", container);
            console.log("loadTags() arg: tag item template", tagItemTemplate);
            console.log("loadTags() arg: selectedTags", selectedTags);
            if (!container || !tagItemTemplate || !selectedTags) {
                console.error("At least one loadTags() argument is undefined")
                return;
            }
            $.getJSON(
                '/tags/all',
                function success(data) {
                    container.children().remove();
                    var selection = {};
                    for (var i = 0; i < selectedTags.length; i++) {
                        selection[selectedTags[i]] = true;
                    }
                    for (var j = 0; j < data.length; j++) {
                        if (selection[data[j].tag]) {
                            data[j].selected = true;
                        } else {
                            data[j].selected = false;
                        }
                    }
                    tagItemTemplate.tmpl(data).appendTo(container);
                    container.trigger("liszt:updated");
                }
            );
        }

    };
})();
