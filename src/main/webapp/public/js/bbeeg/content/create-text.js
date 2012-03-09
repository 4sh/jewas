function TextContentCreator(saveButton, initialContent) {

    /**
     * Rich Text editor instance.
     */
    var textEditor = null;

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

        return !isNullOrEmpty(contentTitle)
            && !isNullOrEmpty(contentDescription);

    }

    function saveContent(form) {
        textEditor.post();

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
    },

    this.initialyzeTextEditor = function() {
        textEditor = new TINY.editor.edit('textEditor',{
            id:'content', // (required) ID of the textarea
            width:655, // (optional) width of the editor
            height:200, // (optional) heightof the editor
            cssclass:'te', // (optional) CSS class of the editor
            controlclass:'tecontrol', // (optional) CSS class of the buttons
            rowclass:'teheader', // (optional) CSS class of the button rows
            dividerclass:'tedivider', // (optional) CSS class of the button diviers
            controls:['bold', 'italic', 'underline', 'strikethrough', '|', 'subscript', 'superscript', '|', 'orderedlist', 'unorderedlist', '|' ,'outdent' ,'indent', '|', 'leftalign', 'centeralign', 'rightalign', 'blockjustify', '|', 'unformat', '|', 'undo', 'redo', 'n', 'font', 'size', 'style', '|', 'hr', '|', 'print'], // (required) options you want available, a '|' represents a divider and an 'n' represents a new row
            footer:true, // (optional) show the footer
            fonts:['Arial'],  // (optional) array of fonts to display
            xhtml:true, // (optional) generate XHTML vs HTML
            cssfile:'/public/css/tinyeditor/style.css', // (optional) attach an external CSS file to the editor
            //content:'', // (optional) set the starting content else it will default to the textarea content
            css:'body{background-color:#ccc}', // (optional) attach CSS to the editor
            bodyid:'editor', // (optional) attach an ID to the editor body
            footerclass:'tefooter', // (optional) CSS class of the footer
            toggle:{text:'source',activetext:'wysiwyg',cssclass:'toggle'}, // (optional) toggle to markup view options
            resize:{cssclass:'resize'} // (optional) display options for the editor resize
        });
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