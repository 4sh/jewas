$(
    function() {
        var mainMenu = $("#mainMenu");

        $("#dashboardMenuItem").click(
            function () {
                if (!$(this).hasClass("menu-item-selected")) {
                    mainMenu.children(".menu-item-selected").removeClass("menu-item-selected");
                    $(this).addClass("menu-item-selected");
                    window.location = "/dashboard/dashboard.html";
                }
            }
        );

        $("#searchMenuItem").click(
            function () {
                if (!$(this).hasClass("menu-item-selected")) {
                    mainMenu.children(".menu-item-selected").removeClass("menu-item-selected");
                    $(this).addClass("menu-item-selected");
                    window.location = "/content/search.html";
                }
            }
        );
    }
);