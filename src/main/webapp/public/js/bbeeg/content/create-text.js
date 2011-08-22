function loadDomains() {
    $.getJSON(
        '/domain/popular?number=20',
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
});