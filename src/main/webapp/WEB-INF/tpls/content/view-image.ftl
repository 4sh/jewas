<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css"]
        useChosen=true>

    <@viewContentHeaders content=content />
    <div class="content_picture"><img src="${content.url()}" class="max_width"/></div>

</@mainTemplate>