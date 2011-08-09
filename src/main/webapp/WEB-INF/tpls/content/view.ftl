<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem="search">

    <@viewContentHeaders content=content />
    <p>Contenu : ${content.text()}</p>
    <iframe src="${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.cliOptions().visioRootUrl()}/visio.html?id=${content.id()}" width="100%" height="100%">
      <p>Your browser does not support iframes.</p>
    </iframe>

</@mainTemplate>