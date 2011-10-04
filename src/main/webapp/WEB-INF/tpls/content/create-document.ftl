<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>

    <script type="text/javascript">
        function createDocumentPrevisualizationObject(url) {
            var object = document.createElement("object");
            object.type = "application/pdf";
            object.data = url;
            object.width = "100%";
            object.height = "100%";
            
            return object;
        }
    </script>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="DOCUMENT" extensions="pdf" extensionsMsgError="Seuls le format PDF est supporté" createPrevisualizationObject="createDocumentPrevisualizationObject"/>

</@mainTemplate>