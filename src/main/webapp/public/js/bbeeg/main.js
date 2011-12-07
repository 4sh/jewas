function getConnectedUserNames(container) {
    $.getJSON(
        '/connectedUser',
        function (data) {
            $(container).children().remove();
            $("#userConnectedNameTemplate").tmpl(data).appendTo(container);
        });
}

$(
    function() {
        getConnectedUserNames($('#connectedUser'));

        /* This function is used to load a item menu. It select the menu and load it's content. */
        var loadMenuItem = function (menuItemElement, url) {
            if (!$(menuItemElement).hasClass('menu-item-selected')) {
                window.location = url;
            }
        };

        // Clickable menu items
        $.each([
                ['#dashboardMenuItem', '/dashboard/dashboard.html'],
                ['#parametersMenuItem', '/user/profile.html']
            ],function(index, value){
                $(value[0]).click(function(){
                    loadMenuItem(this, value[1]);
                });
            }
        );

        $('#disconnectMenuItem').click(function() {
            console.log("Logout called");
            $.ajax(
                {
                    type: "GET",
                    url: '/logout',
                    dataType: 'html',
                    success: function success(response) {
                        window.location = '/login.html';
                        console.log("Logout success");
                    }
                }
            );
        });

        // Root menu item displaying a submenu
        var rootMenuItemsWithSubmenu = [
                ['.userprofile', '#profileSubMenu']
            ];
        $.each(rootMenuItemsWithSubmenu, function(index,value){
                $(value[0]).click(function(evt){
                    if($(value[1]).is(':visible')){
                        $(value[1]).hide("drop", { direction: "right" }, 500);
                    } else {
                        // Hiding every other root menu item displayed
                        $.each(rootMenuItemsWithSubmenu, function(othersIndex,othersValue){
                            if(value[0] != othersValue[0] && $(othersValue[1]).is(':visible')){
                                $(othersValue[1]).hide("drop", { direction: "right" }, 500);
                            }
                        });
                        $(value[1]).show("drop", { direction: "right" }, 500);
                    }
                });
            }
        );

        // Bind the search bar on the search page
        $('#searchLoop').click(function() {
            var searchUrl = '/content/search.html';
            var fullTextSearch = $('#searchInput').val();

            if (window.location.pathname.match('/content/search.html')) {

                // If the current page is already the search page, performs a simple search
                var simpleSearchPageInput = $('#simpleSearchQuery');
                var simpleSearchButton = $('#simpleSearchButton');

                if (simpleSearchPageInput === null) {
                    console.log("Simple search input not found. Location:", window.location);
                }

                if (simpleSearchButton === null) {
                    console.log("Simple search button not found. Location:", window.location);
                }

                simpleSearchPageInput.val(fullTextSearch);
                simpleSearchButton.click();
            } else {
                // If we are are on a non search page, configure the search and then navigate to show results
                var targetUrl = searchUrl;
                if (fullTextSearch !== "") {
                    targetUrl += '#' + fullTextSearch;
                }
                window.location = targetUrl;
            }
        });
    }
);