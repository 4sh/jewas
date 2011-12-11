<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>
    <div class="create_center">
        <script type="application/javascript">
            function createImagePrevisualizationObject(url) {
                var object = document.createElement("img");
                object.src = url;
                object.style.maxWidth = "100%";

                return object;
            }
        </script>

        <div id="confirmationDialog" title="Succès">
            <#if content??>
                <p>Votre image a été modifiée avec succès !</p>
                <#else>
                <p>Votre image a été créée avec succès !</p>
            </#if>
        </div>

        <#if content??>
            <h3>Modification d'une image</h3>
        <#else>
            <h3>Création d'une image</h3>
        </#if>

        <#include "common/create-content.ftl">
        <@createContent url="/content" type="IMAGE" extensions="png|jpg|jpeg|gif" extensionsMsgError="Seuls les formats PNG, JPG, JPEG et GIF sont supportés" createPrevisualizationObject="createImagePrevisualizationObject"/>

    </div>
</@mainTemplate>