<#include "../common/mainTemplate.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Ecran d'accueil" scripts=[chosenJS, "/public/js/bbeeg/search/search.js"]>
<script>
    $(function() {
        $("#searchComponent").accordion();
        SearchQuery.registerClickableForSimpleSearch($("#simpleSearchButton"), $("#simpleSearchQuery"), $("#searchResultsComponent"));
        SearchQuery.registerClickableForAdvancedSearch($("#advancedSearchButton"), /*$("#simpleSearchQuery")[0], */$("#searchResultsComponent"));
    });
</script>

<div id="searchComponent">
    <h3><a href="#">Recherche simple</a></h3>

    <div>Recherche : <input id="simpleSearchQuery" type="text" name="query"/>
        <input id="simpleSearchButton" type="submit" value="Rechercher"/></div>
    <h3><a href="#">Recherche avancée</a></h3>

    <div>Blah blah blah
        <input id="advancedSearchButton" type="submit" value="Rechercher"/></div>
</div>

<div id="searchResultsComponent">
    No results found yet !
</div>
</@mainTemplate>
