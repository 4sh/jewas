<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>

    <script type="text/javascript">
        function createVideoPrevisualizationObject(url) {
            var object = document.createElement("video");
            object.controls = true;
            object.style.width = "100%";

            var source = document.createElement("source");
            source.src = url;

            object.appendChild(source);

            return object;
        }
    </script>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="VIDEO" extensions="mp4" extensionsMsgError="Seuls le format MP4 est supporté" createPrevisualizationObject="createVideoPrevisualizationObject"/>

</@mainTemplate>