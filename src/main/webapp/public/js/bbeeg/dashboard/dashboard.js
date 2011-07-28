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
        function (data) {$('#lastConnectionDate').append("Dernière date de connexion : " + data)}
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
        '/content/total',
        function (data) {$('#totalNumberOfContent').append(jQuery.parseJSON(data).number + " contenus sont disponibles actuellement sur la plateforme.")}
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

$(
    function() {
        loadLastConnectionDate();
        loadMyContents();
        loadTotalNumberOfContents();
        loadLastAuthors();
    }
);