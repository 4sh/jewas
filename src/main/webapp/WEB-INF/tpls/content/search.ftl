<#include "../common/mainTemplate.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate
    title="Ecran d'accueil"
    selectedMenuItem="search"
    scripts=[chosenJS, "/public/js/bbeeg/search/search.js", "/public/js/bbeeg/common/widgets/chainedSelect.js"]
    stylesheets=["/public/css/chosen/chosen.css", "/public/css/bbeeg/search.css"]>
<script>
    function loadAuthors() {
        $.getJSON(
            '/content/author/all',
            function success(data) {
                var container = $("#adSearchAuthors");
                container.children().remove();
                $("#authorItemTemplate").tmpl(data).appendTo(container);
                $("#adSearchAuthors").trigger("liszt:updated");
            }
        );
    }

    function loadContentTypes() {
        $.getJSON(
            '/content/type/all',
            function success(data) {
                var container = $("#adSearchType");
                container.children().remove();
                $("#contentTypeItemTemplate").tmpl(data).appendTo(container);
                $("#adSearchType").trigger("liszt:updated");
            }
        );
    }


    $(function() {
        loadAuthors();
        loadContentTypes();

        $("#searchComponent").accordion({
            autoHeight: false,
            navigation: true,
            changestart: function(event, ui){
                ui.newContent.css('overflow', 'visible');
                ui.oldContent.css('overflow', 'auto');
            }
        });
        var dates = $( "#from, #to" ).datepicker({
			defaultDate: "",
            dateFormat: "dd-mm-yy",
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				var option = this.id == "from" ? "minDate" : "maxDate",
					instance = $( this ).data( "datepicker" ),
					date = $.datepicker.parseDate(
						instance.settings.dateFormat ||
						$.datepicker._defaults.dateFormat,
						selectedDate, instance.settings );
				dates.not( this ).datepicker( "option", option, date );
			}
		});
        $("#adSearchType").chosen();
        $("#adSearchCriterias").chosen();
        $("#adSearchAuthors").chosen();
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#simpleSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#advancedSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );
        var config = new ChainedSelect.Configuration()
                .ajaxUrlsPerDepth([
                      "/content/criterias?depth=0",
                      "/content/criterias?depth=1&parent={value}",
                      "/content/criterias?depth=2&parent={value}",
                      "/content/criterias?depth=3&parent={value}"
                ]).targetFieldForSelectedOption($("#criterias"))
                .displaySelectionTarget($("#criteriasList"))
                .templateForDisplaySelectionItem($("#criteriaSelectedItem"))
                .selectorForClosingLinkInDisplaySelectionItemTemplate("a.search-choice-close")
                .selectMenuContainer($("#searchCriteriaMenuContainer"));
        var criteriaCombo = new ChainedSelect(config).decorateSelectFieldWithChainedSelectConfiguration($("#adSearchCriterias"));

        // Enabling auto query when scrollbar is at the bottom of the window
        var SCROLLBAR_THRESHOLD = 100; // 100 pixels before the bottom of the screen, we consider the scrollbar is at the bottom
        $(window).scroll(function(args){
            if($("#searchResultsComponent #searchNext").length != 0 // Button "searchNext" exists and is not disabled
                      && $("#searchResultsComponent #searchNext").attr('disabled') != "disabled"){
                var scrollbarIsAtBottom = $(window).scrollTop() + SCROLLBAR_THRESHOLD >= $(document).height() - $(window).height();
                if(scrollbarIsAtBottom){
                    $("#searchNext").click();
                }
            }
        });
    });
</script>

<div id="searchComponent">
    <h3><a href="#">Recherche simple</a></h3>

    <div>
        <form action="/content/search" id="simpleSearchForm">
            <label for="simpleSearchQuery">Recherche</label> : <input id="simpleSearchQuery" type="text" name="query" size="80" />
            <input id="simpleSearchButton" type="submit" value="Rechercher"/>
        </form>
    </div>
    <h3><a href="#">Recherche avancée</a></h3>

    <div>
        <form action="/content/advancedSearch" id="advancedSearchForm">
            <div class="criteria-line">
                <label for="from">Date de création : Entre</label>
                <input type="text" id="from" name="from" />
                <label for="to">et</label>
                <input type="text" id="to" name="to"/>
            </div>
            <div class="criteria-line">
                <div class="criteria-label"><label for="adSearchType">Types de contenu</label> :</div>
                <div class="criteria-field">
                    <select id="adSearchType" name="searchTypes" class="chzn-select side-by-side clearfix" multiple>
                    </select>
                </div>
            </div>
            <div class="criteria-line">
                <div class="criteria-label"><label for="adSearchCriterias">Critères </label> :</div>
                <div class="criteria-field">
                    <input type="hidden" id="criterias" name="criterias" value="" />
                    <div class="chzn-container criteria-field">
                        <ul class="chzn-choices selected-options" id="criteriasList">
                        </ul>
                    </div>
                    <div id="searchCriteriaMenuContainer" class="criteria-field">
                        <select id="adSearchCriterias">
                        </select>
                    </div>
                </div>
            </div>
            <div class="criteria-line">
                <div class="criteria-label"><label for="adSearchAuthors">Auteur</label> :</div>
                <div class="criteria-field">
                    <select id="adSearchAuthors" name="authors" class="chzn-select side-by-side clearfix" multiple>
                    </select>
                </div>
            </div>
            <div class="criteria-line">
                <input id="advancedSearchButton" type="submit" value="Rechercher"/>
            </div>
        </form>
    </div>
</div>

<div id="searchResultsComponent" class="search-results-component">
    Aucun résultat trouvé !
</div>
<script id="criteriaSelectedItem" type="text/x-jquery-tmpl">
    <li class="search-choice"><span>{{= label}}</span><a href="#" name="{{= value}}" class="search-choice-close"></a></li>
</script>
<script id="authorItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= name}} </option>
</script>
<script id="contentTypeItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= title}} </option>
</script>
<script id="contentResult" type="text/x-jquery-tmpl">
    <h3>Résultats de la recherche</h3>

    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <button id="searchNext"><img src="/public/images/ajax/indicator.gif" class="spinner" />Résultats suivants</button>
</script>
<script id="contentLineResult" type="text/x-jquery-tmpl">
    <div class="content-result">
        <div class="content-result-title"><a href="/content/{{= id}}/view.html">{{= title}}</a></div>
        <div class="content-result-author">{{= author.name}}</div>
        <div class="content-result-creation-date">{{= creationDate}}</div>
        <div class="content-result-description">{{= description}}</div>
    </div>
</script>

</@mainTemplate>
