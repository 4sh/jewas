<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>
    <div class="create_center">
        <script type="application/javascript">
            function createAudioPrevisualizationObject(url) {
                var object = document.createElement("audio");
                object.controls = true;

                var source = document.createElement("source");
                source.src = url;

                object.appendChild(source);

                return object;
            }
        </script>

            <div id="saveSuccessDialog" title="Succès">
                <#if content??>
                    <p>Votre fichier audio a été modifié avec succès !</p>
                <#else>
                    <p>Votre fichier audio a été créé avec succès !</p>
                </#if>
            </div>

            <#if content??>
                <h3>Modification d'un fichier audio</h3>
            <#else>
                <h3>Création d'un fichier audio</h3>
            </#if>

        <#include "common/create-content.ftl">
        <@createContent url="/content" type="AUDIO" extensions="mp3" extensionsMsgError="Seuls le format MP3 est supporté" createPrevisualizationObject="createAudioPrevisualizationObject"/>
    </div>
</@mainTemplate>