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
    stylesheets=["/public/css/chosen/chosen.css"]>
<script>
    function loadAuthors() {
        $.getJSON(
            '/content/author/all',
            function success(data) {
                var container = $("#adSearchAuthor");
                container.children().remove();
                $("#authorItemTemplate").tmpl(data).appendTo(container);
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
                $("#adSearchType").chosen();
            }
        );
    }


    $(function() {
        loadAuthors();
        loadContentTypes();

        $("#searchComponent").accordion({
            autoHeight: false,
            navigation: true
        });
        //$("#adSearchDate").datepicker();
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
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#simpleSearchForm"))
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
                      "/content/criterias?depth=2&parent={value}"
                ]).targetFieldForSelectedOption($("#criterias"))
                .displaySelectionTarget($("#criteriasList"))
                .selectMenuContainer($("#searchCriteriaMenuContainer"));
        var criteriaCombo = new ChainedSelect(config).decorateSelectFieldWithChainedSelectConfiguration($("#adSearchCriterias"));
    });
</script>

<div id="searchComponent">
    <h3><a href="#">Recherche simple</a></h3>

    <div>
        <form action="/content/search" id="simpleSearchForm">
            <label for="simpleSearchQuery">Recherche</label> : <input id="simpleSearchQuery" type="text" name="query"/>
            <input id="simpleSearchButton" type="submit" value="Rechercher"/>
        </form>
    </div>
    <h3><a href="#">Recherche avancée</a></h3>

    <div>
        <form action="/content/advancedSearch" id="advancedSearchForm">
            <div style="display:block;">
                <!--<label for="adSearchDate">Date de création :</label>-->
                <!--<input type="text" id="adSearchDate" name="date"/>-->
                <label for="from">Date de création : Entre</label>
                <input type="text" id="from" name="from"/>
                <label for="to">et</label>
                <input type="text" id="to" name="to"/>
            </div>
            <div style="width: 600px; display:block; clear:both;">
                <div style="float:left"><label for="adSearchType">Types de contenu</label> :</div>
                <div style="float:left">
                    <select id="adSearchType" class="chzn-select side-by-side clearfix" multiple style="width: 350px">
                    </select>
                </div>
            </div>
            <div style="width: 600px; display:block; clear:both;">
                <div style="float:left"><label for="adSearchCriterias">Critères </label> :</div>
                <div style="float:left">
                    <input type="hidden" id="criterias" name="criterias" value="" />
                    <div class="chzn-container" style="float:left">
                        <ul class="chzn-choices" id="criteriasList"
                                style="background-color: transparent; border: 0px; background-image: none;">
                        </ul>
                    </div>
                    <div id="searchCriteriaMenuContainer" style="float:left; padding-left: 10px">
                        <select id="adSearchCriterias" style="width: 150px">
                            <option value="Critere 1">Libellé Critere 1</option>
                            <option value="Critere 2">Libellé Critere 2</option>
                            <option value="Critere 3">Libellé Critere 3</option>
                        </select>
                    </div>
                </div>
            </div>
            <div style="display:block; clear: both;"><label for="adSearchAuthor">Auteur</label> : <select
                    id="adSearchAuthor"></select></div>
            <input id="advancedSearchButton" type="submit" value="Rechercher"/>
        </form>
    </div>
</div>

<div id="searchResultsComponent" style="margin-top: 10px;" class="ui-widget">
    Aucun résultat trouvé !
</div>
<script id="authorItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= name}} </option>
</script>
<script id="contentTypeItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= title}} </option>
</script>
<script id="contentResult" type="text/x-jquery-tmpl">
    <h3 style="color: #eb8f00;" class="ui-widget">Résultats de la recherche</h3>

    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <button id="searchNext">Résultats suivants</button>
</script>
<script id="contentLineResult" type="text/x-jquery-tmpl">
    <!-- Yeah I know css direct in html is weird... will externalize this in css after sandboxing -->
    <div class="contentResult" style="margin: 10px;">
        <div style="width: 400px; float:left;" class="ui-widget ui-helper-reset">{{= title}}</div>
        <div style="width: 90px; float:left;" class="ui-widget ui-helper-reset">{{= author}}</div>
        <div style="width: 200px; float:left;" class="ui-widget ui-helper-reset">{{= creationDate}}</div>
        <div style="clear: both; width: 800px; font-style: italic;" class="ui-widget ui-helper-reset">{{= description}}</div>
    </div>
</script>

</@mainTemplate>
