function success(data, container) {
    $(container).children().remove();

    $("#contentItemTemplate").tmpl(jQuery.parseJSON(data)).appendTo(container);
}

function createLastAdded(container) {
    $.getJSON(
        '/content/added/last?number=5',
        function (data) {success(data, container);}
    );
}

function createLastViewed(container) {
    $.getJSON(
        '/content/viewed/last?number=5',
        function (data) {success(data, container);}
    );
}

function createMostPopular(container) {
    $.getJSON(
        '/content/popular?number=5',
        function (data) {success(data, container);}
    );
}


function loadLastConnectionDate() {
    $.getJSON(
        '/user/lastConnectionDate',
        function (data) {$('#lastConnectionDate').append("Dernière date de connexion : " + jQuery.parseJSON(data).date)}
    );
}

function loadMyContents() {
    var tabContainer = new TabContainer('myContents');

    tabContainer.addTab('Nouveaux', createLastAdded);
    tabContainer.addTab('Consultés', createLastViewed);
    tabContainer.addTab('Populaires', createMostPopular);

    $("#user-contents").append(tabContainer.htmlElement());
}

function loadTotalNumberOfContents() {
    $.getJSON(
        '/content/count',
        function (data) {$('#totalNumberOfContent').append(jQuery.parseJSON(data).count + " contenus sont disponibles actuellement sur la plateforme.")}
    );
}

function loadLastAuthors() {
    $.getJSON(
        '/content/author/last?number=5',
        function success(data) {
            var container = $("#lastAuthors");
            container.children().remove();
            $("#authorItemTemplate").tmpl(jQuery.parseJSON(data)).appendTo(container);
        }
    );
}

function loadDomains() {
$.getJSON(
        '/domain/popular?number=20',
        function success(data) {
            var container = $("#domainCloud");
            container.children().remove();
            container.jQCloud(jQuery.parseJSON(data));
        }
    );

}

$(
    function() {
        loadLastConnectionDate();
        loadMyContents();
        loadTotalNumberOfContents();
        loadLastAuthors();
        loadDomains();
    }
);