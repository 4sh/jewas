function loadDomains(domainIds) {
    console.log("domainIds", domainIds);
    $.getJSON(
        '/domain/all',
        function success(data) {
            var container = $("#domains");
            container.children().remove();
            var selectedDomains = {};
            for (var i=0; i < domainIds.length; i++) {
                selectedDomains[domainIds[i]]=true;
            }
            for (var j=0; j < data.length; j++) {
                if (selectedDomains[data[j].id]) {
                    data[j].selected = true;
                } else {
                    data[j].selected = false;
                }
            }
            $("#domainItemTemplate").tmpl(data).appendTo(container);
            $("#domains").trigger("liszt:updated");
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

function getAuthorUserNames(container) {
    $.getJSON(
        '/connectedUser',
        function (data) {
            $(container).children().remove();
            $("#authorItemTemplate").tmpl(data).appendTo(container);
        });
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
            $("#tagItemTemplate").tmpl(data).appendTo(container);
            $("#tags").trigger("liszt:updated");
        }
    );
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

        $.put(form.action,
            dataToSend,
            function(data){
                var contentId = data.id;
                $.put('/content/' + contentId + '/content/text',
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