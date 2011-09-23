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
                ['#parametersMenuItem', '/user/profile.html'],
                ['#manageMyContentsMenuItem', '/content/search-user-content.html'],
                ['#adminContentsMenuItem', '/content/search-content-to-treat.html'],
                ['#createTextMenuItem', '/content/text/create.html'],
                ['#createDocumentMenuItem', '/content/document/create.html'],
                ['#createImageMenuItem', '/content/image/create.html'],
                ['#createVideoMenuItem', '/content/video/create.html'],
                ['#createAudioMenuItem', '/content/audio/create.html'],
                ['#createEegMenuItem', '/content/eeg/create.html']
            ],function(index, value){
                $(value[0]).click(function(){
                    loadMenuItem(this, value[1]);
                });
            }
        );

        // Root menu item displaying a submenu
        var rootMenuItemsWithSubmenu = [
                ['#admin_arrow', '#adminSubMenu'],
                ['#userprofile_arrow', '#profileSubMenu']
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
    }
);