<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>

    <div class="create_center">
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

        <div id="confirmationDialog" title="Succès">
            <#if content??>
                <p>Votre document .PDF a été modifié avec succès !</p>
            <#else>
                <p>Votre document .PDF a été créé avec succès !</p>
            </#if>
        </div>

        <#if content??>
            <h3>Modification d'un document .PDF</h3>
        <#else>
            <h3>Création d'un document .PDF</h3>
        </#if>

        <#include "common/create-content.ftl">
        <@createContent url="/content" type="DOCUMENT" extensions="pdf" extensionsMsgError="Seuls le format PDF est supporté" createPrevisualizationObject="createDocumentPrevisualizationObject"/>
    </div>
</@mainTemplate>