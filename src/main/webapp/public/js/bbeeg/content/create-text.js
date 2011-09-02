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

$(function() {
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

        $.put(form.action,
            JSON.stringify(contentDetail),
            function(data){
                $.put('/content/content/' + data.id + '/TEXT',
                    {text: $('#content')[0].value},
                    function(data){
                        $("#confirmationDialog").dialog('open');
                        setTimeout(function(){
                            $("#confirmationDialog").dialog('close');
                        }, 2000);
                        form.reset();
                    },
                    'text'
                );
            }
        );


        return false;
    });
});