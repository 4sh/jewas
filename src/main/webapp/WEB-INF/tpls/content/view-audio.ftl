<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        stylesheets=["/public/css/bbeeg/view.css"]
        selectedMenuItem=""
        useChosen=true>

    <@viewContentHeaders content=content />
    <p>Contenu :</p> <audio controls="controls"> <source src="${content.url()}"/> </audio>

</@mainTemplate>