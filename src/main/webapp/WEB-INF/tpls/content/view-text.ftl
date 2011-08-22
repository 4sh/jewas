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
    <p>Contenu :</p> <textarea id="content" rows="5" cols="100" readonly="true"></textarea>

</@mainTemplate>