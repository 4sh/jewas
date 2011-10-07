<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        stylesheets=["/public/css/bbeeg/view.css"]
        selectedMenuItem=""
        useChosen=true>

    <@viewContentHeaders content=content />
    <div class="content_audio"><audio controls="controls"> <source src="${content.url()}"/> </audio></div>

</@mainTemplate>