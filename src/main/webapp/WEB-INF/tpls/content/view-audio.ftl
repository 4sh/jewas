<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        useChosenCSS=true useChosenJS=true>

    <@viewContentHeaders content=content />
    <p>Contenu :</p> <audio controls="controls"> <source src="${content.url()}"/> </audio>

</@mainTemplate>