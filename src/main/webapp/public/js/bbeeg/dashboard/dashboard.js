function success(data, container) {
    $(container).children().remove();

    $("#contentItemTemplate").tmpl(data).appendTo(container);
    // Activate scrolling on mouse over, deactivate on mouse out
    container.find('.ui-panel-content-text').mouseenter(
        function() {
            var contentDiv = $(this);
            var titleAnchor = contentDiv.find(".content_title");

            if (!titleAnchor.parent().hasClass('content_title_container')) {
                return;
            }
            if (titleAnchor.text().length > 32) {
                titleAnchor.wrap("<marquee behavior='scroll' direction='left' scrollamount='2' width='200'>");
                $('marquee').marquee();
            }
        }).mouseleave(function () {
            var contentDiv = $(this);
            var titleAnchor = contentDiv.find(".content_title");
            if (titleAnchor.parent().hasClass('content_title_container')) {
                return;
            }
            titleAnchor.parent().trigger('stop');
            titleAnchor.unwrap();
            titleAnchor.unwrap();
        });
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

function loadMySpace(container) {
    $.getJSON(
        '/connectedUser',
        function (data) {
            $(container).children().remove();
            $("#mySpaceTemplate").tmpl(data).appendTo($(container));
        });

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
        function (data) {$('#totalNumberOfContent').append("<b>"+data.count + " contenus</b> sont disponibles actuellement sur la plateforme.")}
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
        '/tag/popular?number=100',
        function success(data) {
            var items = [];
            $.each(data, function(index, value) {
                items.push({text: value.tag, weight: value.weight, url: "/content/search.html#" + value.tag});
            });

            var container = $("#tagList");
            container.children().remove();
            $("#tagItemTemplate").tmpl(items).appendTo(container);
            container.tagcloud({type:"list",sizemin:14, colormin:"434342",colormax:"3D3D3D"}).find("li").tsort({});
        });
}

$(
    function() {
        loadMySpace($('#mySpace'));
        loadMyContents();
        loadTotalNumberOfContents();
        loadLastAuthors();
        loadTags();
    }
);