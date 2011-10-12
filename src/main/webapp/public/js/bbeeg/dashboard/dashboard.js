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

/*function loadUserDomains() {
    $.getJSON(
        '/user/domains/5',
        function (data) {
            $('#user-domains').children().remove();
            $("#userDomainItemTemplate").tmpl(data).appendTo($('#user-domains'));
        }
    );
}*/

function loadMyActions() {
    $.getJSON(
        '/myActions/',
        function success(data) {
            var container = $("#myActions");
            container.children().remove();
            $("#actionItemTemplate").tmpl(data).appendTo(container);
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
    createMostPopular($("#user-contents-advisable"));
}

function loadTotalNumberOfContents() {
    $.getJSON(
        '/content/count',
        function (data) {$('#totalNumberOfContent').append(data.count + " contenus sont disponibles actuellement sur la plateforme.")}
    );
}

function loadLastAuthors() {
    $.getJSON(
        '/users/authors/last?number=5',
        function success(data) {
            var container = $("#lastAuthors");
            container.children().remove();
            $("#authorItemTemplate").tmpl(data).appendTo(container);
        }
    );
}

function loadTags() {
    $.getJSON(
        '/tag/popular?number=20',
        function success(data) {
            var items = [];
            $.each(data, function(index, value) {
                items.push({text: value.tag, weight: value.weight, url: "/content/search.html#" + value.tag});
            });

            var container = $("#domainCloud");
            container.children().remove();
            container.jQCloud(items);
        }
    );
}

$(
    function() {
        //loadMyActions();
        loadMyContents();
        loadTotalNumberOfContents();
        loadLastAuthors();
        loadTags();
    }
);