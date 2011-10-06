<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css"]
        useChosen=true>
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
    <p>Contenu :</p> <div id="content"></div>

</@mainTemplate>