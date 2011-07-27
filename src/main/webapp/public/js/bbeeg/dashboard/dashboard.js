function success(data, container) {
    $(container).children().remove();

    $.each( jQuery.parseJSON(data),
        function (key, val) {
            $("#contentItemTemplate").tmpl(val).appendTo(container);
        }
    );
}

function createLastAdded(container) {
    $.getJSON(
        '/content/lastAdded?number=5',
        function (data) {success(data, container);}
    );
}

function createLastViewed(container) {
    $.getJSON(
        '/content/lastViewed?number=6',
        function (data) {success(data, container);}
    );
}

function createMostPopular(container) {
    $.getJSON(
        '/content/mostPopular?number=7',
        function (data) {success(data, container);}
    );
}


function loadLastConnectionDate() {
    $.getJSON(
        '/user/lastConnectionDate',
        function (data) {$('#lastConnectionDate').append(data)}
    );
}

function createMyContents() {
    var tabContainer = new TabContainer('myContents');

    tabContainer.addTab('Nouveaux', createLastAdded);
    tabContainer.addTab('Consultés', createLastViewed);
    tabContainer.addTab('Populaires', createMostPopular);

    $("#user-contents").append(tabContainer.htmlElement());
}

$(
    function() {
        loadLastConnectionDate();
        createMyContents();
    }
);