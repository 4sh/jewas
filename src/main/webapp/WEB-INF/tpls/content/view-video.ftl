<#include "../common/mainTemplate.ftl">
<#include "common/view-content-headers.ftl">

<@mainTemplate title="Consultation"
        selectedMenuItem=""
        stylesheets=["/public/css/bbeeg/view.css"]
        scripts=["/public/js/bbeeg/content/video.js"]
        useChosen=true>
    <@viewContentHeaders content=content />
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
    </script>

    <div class="content_video">
        <video id="videoTagId" controls="controls" onerror="failed(event)">
            <source src="${content.url()}" class="max_width"/>
        </video>
    </div>

    <div id="errorDialog" title="Erreur de lecture">
        <span>La vidéo sélectionnée ne peut être lue dans son encodage actuel.</span>
        <br/>
        <br/>
        <span>Elle sera disponible en consultation dans 24h au plus tard. Au-delà de ce délai, si la vidéo est toujours indisponible, veuillez contacter l'administrateur système.</span>
    </div>
</@mainTemplate>