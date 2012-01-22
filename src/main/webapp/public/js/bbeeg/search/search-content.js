function extractSearchCriterionFromUrl() {
    var url = window.location.href;
    if (url.indexOf('#') !== -1) {
        var splittedUrl = url.split('#');

        if (splittedUrl.length !== 2) {
            console.log("Error url should only contain one #. url:", url);
            return;
        }

        $('#simpleSearchQuery').val(decodeURI(splittedUrl[1]));
        $('#simpleSearchButton').click();
    }
}

function formatDescription(text) {
    if (text !== null) {
        return text.substring(0, 220).concat(" ...");
    }
}

function process(results) {
    $(results).each(
        function (i, e) {
            if (!e) {
                return;
            }
            var statusStyle = contentHelper.getStatusStyle(e.status);
            e.statusLabel = statusStyle.label;
            e.statusClass = statusStyle.className;
        }
    );
    return results;
}

function loadAllDomains() {
    $.getJSON(
        '/domain/all',
        function success(data) {
            var container = $("#adSearchDomains");
            container.children().remove();
            $("#domainItemTemplate").tmpl(data).appendTo(container);
            $("#adSearchDomains").trigger("liszt:updated");
        }
    );
}

function loadAllContentTypes() {
    $.getJSON(
        '/content/type/all',
        function success(data) {
            var container = $("#adSearchType");
            container.children().remove();
            $("#contentTypeItemTemplate").tmpl(data).appendTo(container);
            $("#adSearchType").trigger("liszt:updated");
        }
    );
}

function loadAllAuthors() {
    $.getJSON(
            '/users/authors/all',
            function success(data) {
                var container = $("#adSearchAuthors");
                container.children().remove();
                $("#authorItemTemplate").tmpl(data).appendTo(container);
                $("#adSearchAuthors").trigger("liszt:updated");
            }
    );
}

function viewContent(contentId) {
    if (contentId !== null) {
        window.location = "/content/" + contentId + "/view.html";
    } else {
        console.log("ContentId not found", contentId)
    }
}





