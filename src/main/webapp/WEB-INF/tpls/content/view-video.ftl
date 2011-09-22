<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        useChosen=true>
    <@viewContentHeaders content=content />
    <p>Contenu :</p> <video controls="controls" width="100%"> <source src="${content.url()}"/> </video>

</@mainTemplate>