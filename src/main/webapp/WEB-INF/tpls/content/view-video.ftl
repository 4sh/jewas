<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css"]
        scripts=["/public/js/bbeeg/content/video.js"]
        useChosen=true>
    <@viewContentHeaders content=content />
    <div class="content_video">
        <video id="videoTagId" controls="controls" onerror="fallback">
            <source src="${content.url()}" class="max_width"/>
            video not supported
        </video>
    </div>
</@mainTemplate>