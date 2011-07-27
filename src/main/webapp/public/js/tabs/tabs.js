function TabContainer(id) {
    var globalContainer;
    var menu;
    var content;

    this.htmlElement = function () {return globalContainer};

    this.addTab = function (name, loadContent) {
        var tab = document.createElement('div');
        tab.textContent = name;
        $(tab).addClass("tab-item").click(
            function () {
                if (!$(this).hasClass("tab-selected")) {
                    $(menu).children(".tab-selected").removeClass("tab-selected");
                    $(this).addClass("tab-selected");
                    loadContent(content);
                }
            }
        );

        if (!menu.hasChildNodes()) {
            $(tab).addClass("tab-selected");
            loadContent(content);
        }

        menu.appendChild(tab);

        return tab;
    };

	(function(id) {
		 globalContainer = document.createElement('div');
		 globalContainer.id = id;
		 $(globalContainer).addClass = "tab-container";

        menu = document.createElement('div');
        $(menu).addClass("tab-menu");

        content = document.createElement('div');
        $(content).addClass("tab-content");

        globalContainer.appendChild(menu);
        globalContainer.appendChild(content);

        return globalContainer;
	}).call(this);
}