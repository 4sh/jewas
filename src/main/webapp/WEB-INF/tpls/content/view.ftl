<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem="search">

    <iframe src="${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.cliOptions().visioRootUrl()}/visio.html?id=${id}" width="100%" height="100%">
      <p>Your browser does not support iframes.</p>
    </iframe>

</@mainTemplate>