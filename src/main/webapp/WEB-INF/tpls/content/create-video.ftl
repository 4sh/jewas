<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js",
                        "/public/js/bbeeg/content/video.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>
    <div class="create_center">
        <script type="application/javascript">
            $(function() {
                $("#errorDialog").dialog({
                    buttons: [
                        { text: "ok",
                            click: function() {
                                $(this).dialog("close");
                            }
                        }],
                    autoOpen: false,
                    modal: false,
                    show: 'drop',
                    hide: 'drop',
                    width: "50%"
                });
            });

            function createVideoPrevisualizationObject(url) {
                var object = document.createElement("video");
                object.id = "videoTagId";
                
                object.controls = true;
                object.style.maxWidth = "100%";
                object.onerror = failed;

                var source = document.createElement("source");
                source.src = url;

                object.appendChild(source);
                return object;
            }
        </script>

        <div id="saveSuccessDialog" title="Succès">
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

        <div id="errorDialog" title="Erreur de lecture">
            <span>La vidéo sélectionnée ne peut être lue dans son encodage actuel.</span>
            <br/>
            <br/>
            <span>Vous pouvez l'enregistrer mais elle ne sera disponible en consultation que dans 24h au plus tard. Au-delà de ce délai, si la vidéo est toujours indisponible, veuillez contacter l'administrateur système.</span>
        </div>

        <#include "common/create-content.ftl">
        <@createContent url="/content" type="VIDEO" extensions="mp4" extensionsMsgError="Seuls le format MP4 est supporté" createPrevisualizationObject="createVideoPrevisualizationObject"/>
    </div>
</@mainTemplate>