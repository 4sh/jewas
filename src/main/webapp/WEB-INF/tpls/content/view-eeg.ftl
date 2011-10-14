<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css", "/public/css/bbeeg/view-eeg.css"]>

    <script type="text/javascript">
        $(
            function () {
                $.ajax({
                        url: "${content.url()}",
                        success: function (data) {
                            $("#content").append(data);
                        }
                });
            }
        );
    </script>

    <@viewContentHeaders content=content />
    <iframe src="${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.cliOptions().visioRootUrl()}/${content.header().id()?c}/visio.html" width="1040px" height="800px" style="border: none;">
      <p>Your browser does not support iframes.</p>
    </iframe>

</@mainTemplate>