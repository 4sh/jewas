$(
    function() {
        var applicationMenu = $("#applicationMenu");
        var configurationMenu = $("#configurationMenu");

        /* This function is used to load a item menu. It select the menu and load it's content. */
        var loadMenuItem = function (menuItemElement, url) {
            if (!$(menuItemElement).hasClass("menu-item-selected")) {
                applicationMenu.children(".menu-item-selected").removeClass("menu-item-selected");
                configurationMenu.children(".menu-item-selected").removeClass("menu-item-selected");
                $(menuItemElement).addClass("menu-item-selected");
                window.location = url;
            }
        };

        $("#dashboardMenuItem").click(
            function () {
                loadMenuItem(this, "/dashboard/dashboard.html");
            }
        );

        $("#searchMenuItem").click(
            function () {
                loadMenuItem(this, "/content/search.html");
            }
        );

        $("#profileMenuItem").click(
            function () {
                loadMenuItem(this, "/user/profile.html");
            }
        );

    }
);