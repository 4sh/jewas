<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css"]
        useChosen=true>
    <@viewContentHeaders content=content />
    <div class="content_video"><video controls="controls"> <source src="${content.url()}" class="max_width"/> </video></div>

</@mainTemplate>