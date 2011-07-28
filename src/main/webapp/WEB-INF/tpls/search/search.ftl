<#include "../common/mainTemplate.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Ecran d'accueil"
selectedMenuItem="search"
scripts=[chosenJS, "/public/js/bbeeg/search/search.js"]>
<script>
    $(function() {
        $("#searchComponent").accordion();
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#simpleSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );
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

    <div>Blah blah blah
        <input id="advancedSearchButton" type="submit" value="Rechercher"/></div>
</div>

<div id="searchResultsComponent">
    No results found yet !
</div>

<script id="contentResult" type="text/x-jquery-tmpl">
    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <button id="searchNext">Résultats suivants</button>
</script>
<script id="contentLineResult" type="text/x-jquery-tmpl">
    <!-- Yeah I know css direct in html is weird... will externalize this in css after sandboxing -->
    <div class="contentResult" style="margin: 10px;">
        <div style="width: 200px; float:left;">{{= title}}</div>
        <div style="witdh: 400px; float:left;">{{= author}}</div>
        <div
                style="width: 300px; float:left;">{{= lastModificationDate}}
        </div>
        <div style="clear: both; width: 800px; font-style: italic;">{{= description}}</div>
    </div>
</script>

</@mainTemplate>
