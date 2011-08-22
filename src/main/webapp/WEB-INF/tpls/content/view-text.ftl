<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        scripts=[chosenJS]
        stylesheets=["/public/css/chosen/chosen.css"]>

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
    <p>Contenu :</p> <textarea id="content" rows="5" cols="100" readonly="true"></textarea>

</@mainTemplate>