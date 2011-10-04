<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>

    <script type="text/javascript">
        function createAudioPrevisualizationObject(url) {
            var object = document.createElement("audio");
            object.controls = true;

            var source = document.createElement("source");
            source.src = url;

            object.appendChild(source);

            return object;
        }
    </script>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="AUDIO" extensions="mp3" extensionsMsgError="Seuls le format MP3 est supporté" createPrevisualizationObject="createAudioPrevisualizationObject"/>

</@mainTemplate>