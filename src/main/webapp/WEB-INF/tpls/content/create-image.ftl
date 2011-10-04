<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>

    <script type="text/javascript">
        function createImagePrevisualizationObject(url) {
            var object = document.createElement("img");
            object.src = url;
            object.style.width = "100%";

            return object;
        }
    </script>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="IMAGE" extensions="png|jpg|pjeg|gif" extensionsMsgError="Seuls les formats PNG, JPEG et GIF sont supportés" createPrevisualizationObject="createImagePrevisualizationObject"/>

</@mainTemplate>