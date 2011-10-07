<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css"]
        useChosen=true>

    <@viewContentHeaders content=content />
    <#--<p>Contenu :</p> <img src="${content.url()}" width="100%"/>-->
<object data="${content.url()}" type="application/pdf" width="100%" height="100%"/>


</@mainTemplate>