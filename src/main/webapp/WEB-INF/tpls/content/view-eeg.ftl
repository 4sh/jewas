<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem="">

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
    <iframe src="${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.cliOptions().visioRootUrl()}/${content.header().id()?c}/visio.html" width="100%" height="100%">
      <p>Your browser does not support iframes.</p>
    </iframe>

</@mainTemplate>