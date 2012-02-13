function TextContentCreator(saveButton, initialContent) {

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
        var textContent = $("#content").val();

        return !isNullOrEmpty(contentTitle)
            && !isNullOrEmpty(contentDescription)
            && !isNullOrEmpty(textContent);

    }

    function saveContent(form) {
        var contentDetail = {
            header:{
                title:$("#title").val(),
                description:$("#description").val(),
                domains:getDomains($("#domains").val()),
                tags:$("#tags").val()
            }
        };

        var dataToSend = {
            type : 'TEXT',
            contentDetail : JSON.stringify(contentDetail)
        };

        $.ajaxPut(form.action,
            dataToSend,
            function (data) {
                var contentId = data.id;
                $.ajaxPut('/content/' + contentId + '/content/text',
                    {text:$('#content')[0].value},
                    function () {
                        $("#saveSuccessDialog").dialog('open');
                        setTimeout(function () {
                            $("#saveSuccessDialog").dialog('close');
                            window.location.href = "/content/" + contentId + "/view.html";
                        }, 2000);
                    },
                    'text'
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
    };

    /** Constructor **/
    (function () {
        $("#confirmationDialog").dialog({
            autoOpen:false,
            show:'slide',
            hide:'slide',
            width:'40%',
            buttons:[
                {
                    text:"Ok",
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
                    text:"Annuler",
                    click:function () {
                        $(this).dialog("close");
                    }
                }
            ]
        });

        $("#saveSuccessDialog").dialog({
            autoOpen:false,
            modal:false,
            show:'drop',
            hide:'drop'
        });

        getConnectedUserNames($("#author"));

        $("#domains").chosen();
        $("#tags").chosen();

        $("#saveBtn").click(function () {
            $("#confirmationDialog").dialog('open');
        });
    })();
}