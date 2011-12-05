<#include "../common/mainTemplate.ftl">
<#--<#include "common/view-content-headers.ftl">-->
<#include "eeg-content-header.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view-eeg.css"]>

    <@eegContentHeader content=content />
    <iframe src="${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.cliOptions().visioRootUrl()}/${content.header().id()?c}/visio.html" width="1040px" height="800px" style="border: none;">
      <p>Your browser does not support iframes.</p>
    </iframe>

</@mainTemplate>