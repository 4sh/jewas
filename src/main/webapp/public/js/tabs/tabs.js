function TabContainer(id) {
    var globalContainer;
    var menu;
    var content;

    this.htmlElement = function () {return globalContainer};

    this.addTab = function (name, loadContent) {
        var tab = document.createElement('div');
        tab.textContent = name;

        $(tab).addClass("tab-item")
              .addClass("ui-state-default")
              .addClass("ui-corner-top")
              .click(
                function () {
                    if (!$(this).hasClass("ui-tabs-selected")) {
                        $(menu).children(".ui-tabs-selected")
                               .removeClass("ui-tabs-selected")
                               .removeClass("ui-state-active");
                        $(this).addClass("ui-tabs-selected")
                               .addClass("ui-state-active");
                        loadContent(content);
                    }
                }
        );

        if (!menu.hasChildNodes()) {
            $(tab).addClass("ui-tabs-selected")
                  .addClass("ui-state-active");
            loadContent(content);
        }

        menu.appendChild(tab);

        return tab;
    };

	(function(id) {
		 globalContainer = document.createElement('div');
		 globalContainer.id = id;
		 $(globalContainer).addClass("tab-container")
		                   .addClass("ui-tabs")
		                   .addClass("ui-widget")
		                   .addClass("ui-widget-content")
		                   .addClass("ui-corner-all");

        menu = document.createElement('div');
        $(menu).addClass("tab-menu")
               .addClass("ui-tabs-nav")
               .addClass("ui-helper-reset")
               .addClass("ui-helper-clearfix")
               .addClass("ui-corner-top");


        content = document.createElement('div');
        $(content).addClass("tab-content");

        globalContainer.appendChild(menu);
        globalContainer.appendChild(content);

        return globalContainer;
	}).call(this);
}