function success(data, container) {
    $(container).children().remove();

    $("#contentItemTemplate").tmpl(data).appendTo(container);
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


//function loadLastConnectionDate() {
//    $.getJSON(
//        '/user/lastConnectionDate',
//        function (data) {$('#lastConnectionDate').append("Dernière date de connexion : " + data.date)}
//    );
//}

function loadUserDomains() {
    $.getJSON(
        '/user/domains/5',
        function (data) {
            $('#user-domains').children().remove();
            $("#userDomainItemTemplate").tmpl(data).appendTo($('#user-domains'));
        }
    );
}

function loadMyContents() {
    /*
    var tabContainer = new TabContainer('myContents');

    tabContainer.addTab('New', createLastAdded);
    tabContainer.addTab('Viewed', createLastViewed);
    tabContainer.addTab('Popular', createMostPopular);

    $("#user-contents").append(tabContainer.htmlElement());
    */
    createLastAdded($("#user-contents-last-added"));
    createLastViewed($("#user-contents-last-viewed"));
    createMostPopular($("#user-contents-populars"));
}

function loadTotalNumberOfContents() {
    $.getJSON(
        '/content/count',
        function (data) {$('#totalNumberOfContent').append(data.count + " contenus sont disponibles actuellement sur la plateforme.")}
    );
}

function loadLastAuthors() {
    $.getJSON(
        '/content/author/last?number=5',
        function success(data) {
            var container = $("#lastAuthors");
            container.children().remove();
            $("#authorItemTemplate").tmpl(data).appendTo(container);
        }
    );
}

function loadDomains() {
    $.getJSON(
        '/domain/popular?number=20',
        function success(data) {
            var container = $("#domainCloud");
            container.children().remove();
            container.jQCloud(data);
        }
    );
}

function buildPanels() {
    $.each(["#myContentsPanelLastAdded", "#myContentsPanelLastViewed", "#myContentsPanelPopulars",
        "#myLessonsPanel", "#myTestsPanel", "#plateformInformationsPanel", "#domainsPanel", "#myDomainsPanel"],
        function(index, value){
            $(value).panel({
                'collapsible':false
            });
        }
    );
}

$(
    function() {
        loadUserDomains();
        loadMyContents();
        loadTotalNumberOfContents();
        loadLastAuthors();
        loadDomains();
        buildPanels();
    }
);