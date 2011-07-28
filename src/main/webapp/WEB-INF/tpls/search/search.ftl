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
        SearchQuery.registerSearch(
                new SearchQuery.SearchContext().targetForm($("#simpleSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .resultTemplate($("#contentResult"))
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
    <div class="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
</script>
<script id="contentLineResult" type="text/x-jquery-tmpl">
    <div class="contentResult">
        <span style="width: 200px">{{= title}}</span><span style="witdh: 400px">{{= author}}</span><span
            style="width: 300px">{{= lastModificationDate}}</span>
        <span style="clear: both; width: 800px;">{{= description}}</span>
    </div>
</script>

</@mainTemplate>
