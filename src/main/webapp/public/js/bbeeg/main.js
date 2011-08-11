$(
    function() {
        /*
        var applicationMenu = $('#applicationMenu');
        var configurationMenu = $('#configurationMenu');
        */

        /* This function is used to load a item menu. It select the menu and load it's content. */
        var loadMenuItem = function (menuItemElement, url) {
            if (!$(menuItemElement).hasClass('menu-item-selected')) {
//                applicationMenu.children('.menu-item-selected').find('.selection-indicator').remove();
//                configurationMenu.children('.menu-item-selected').find('.selection-indicator').remove();
//
//                applicationMenu.children('.menu-item-selected').removeClass('menu-item-selected');
//                configurationMenu.children('.menu-item-selected').removeClass('menu-item-selected');
//
//                $(menuItemElement).addClass('menu-item-selected');

//                var element = document.createElement('span');
//                $(element).addClass('selection-indicator');
//                $(menuItemElement).append(element);

                window.location = url;
            }
        };

        // Clickable menu items
        $.each([
                ['#dashboardMenuItem', '/dashboard/dashboard.html'],
                ['#searchMenuItem', '/content/search.html'],
                ['#profileMenuItem', '/user/profile.html'],
                ['#createContentMenuItem', '/content/text/create.html']
            ],function(index, value){
                $(value[0]).click(function(){
                    loadMenuItem(this, value[1]);
                });
            }
        );

        // Root menu item displaying a submenu
        $.each([
                ['#adminMenuItem', '#adminSubMenu']
            ],function(index,value){
                $(value[0]).mouseenter(function(evt){
                    $(value[1]).show("drop", { direction: "right" }, 500);
                }).mouseleave(function(evt){
                    $(value[1]).hide("drop", { direction: "right" }, 500);
                });
            }
        );
    }
);