

function createLastAdded(container) {
    $.getJSON('/content/lastAdded',
        function(data){

            $.each( jQuery.parseJSON(data),
                    function (key, val) {
                        $("#lastAddedItemTemplate").tmpl(val).appendTo(container);
                        console.log(container);
                    }
            );
        }
    );
}

function createLastViewed(container) {
    $.ajax({
        url: "/content/lastViewed?number=5", // TODO: create the route
        success: function(data){
            // TODO: get the jquery template
        }
    });
}

function createMostPopular(container) {
    $.ajax({
        url: "/content/popular?number=5", // TODO: create the route
        success: function(data){
            // TODO: get the jquery template
        }
    });
}


function createMyContents() {
    var tabContainer = new TabContainer('dfdf');

    tabContainer.addTab('Les derniers ajoutés', createLastAdded);
    tabContainer.addTab('Les derniers consultés', createLastViewed);
    tabContainer.addTab('Les plus populaires', createMostPopular);

    $("#user-contents").append(tabContainer.htmlElement());
}

$(
    function() {
        createMyContents();
    }
);