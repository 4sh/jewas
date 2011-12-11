<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js",
                        "/public/js/bbeeg/content/video.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>
    <div class="create_center">
        <script type="application/javascript">
            function createVideoPrevisualizationObject(url) {
                var object = document.createElement("video");
                object.id = "videoTagId";
                
                object.controls = true;
                object.style.maxWidth = "100%";
                object.onerror= fallback;

                var source = document.createElement("source");
                source.src = url;

                object.appendChild(source);

                return object;
            }
        </script>

        <div id="confirmationDialog" title="Succès">
            <#if content??>
                <p>Votre vidéo a été modifiée avec succès !</p>
            <#else>
                <p>Votre vidéo a été créée avec succès !</p>
            </#if>
        </div>

        <#if content??>
            <h3>Modification d'une vidéo</h3>
        <#else>
            <h3>Création d'une vidéo</h3>
        </#if>


        <#include "common/create-content.ftl">
        <@createContent url="/content" type="VIDEO" extensions="mp4" extensionsMsgError="Seuls le format MP4 est supporté" createPrevisualizationObject="createVideoPrevisualizationObject"/>
    </div>
</@mainTemplate>