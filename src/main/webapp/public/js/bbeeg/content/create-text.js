function getDomains(domainIds) {
    var domains = [];

    if (domainIds !== null) {
        for (var i=0; i < domainIds.length; i++) {
            domains.push({id: domainIds[i]});
        }
    }
    return domains;
}

function getAuthorUserNames(container) {
    $.getJSON(
        '/connectedUser',
        function (data) {
            $(container).children().remove();
            $("#authorItemTemplate").tmpl(data).appendTo(container);
        });
}

$(function() {

    $("#confirmationDialog").dialog({
        autoOpen: false,
        modal: false,
        show: 'drop',
        hide: 'drop'
    });

    getAuthorUserNames($("#author"));
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
            type : 'TEXT',
            contentDetail : JSON.stringify(contentDetail)
        };

        $.ajaxPut(form.action,
            dataToSend,
            function(data){
                var contentId = data.id;
                $.ajaxPut('/content/' + contentId + '/content/text',
                    {text: $('#content')[0].value},
                    function(data){
                        $("#confirmationDialog").dialog('open');
                        setTimeout(function(){
                            $("#confirmationDialog").dialog('close');
                        }, 2000);
                        window.location.href = "/content/" + contentId + "/view.html";
                    },
                    'text'
                );
            }
        );
        return false;
    });
});