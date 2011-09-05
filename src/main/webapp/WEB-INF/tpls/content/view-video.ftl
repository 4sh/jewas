<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        scripts=[chosenJS]
        stylesheets=["/public/css/chosen/chosen.css"]>

    <@viewContentHeaders content=content />
    <p>Contenu :</p> <video controls="controls" width="100%"> <source src="${content.url()}"/> </video>

</@mainTemplate>