<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosenCSS=true useChosenJS=true>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="AUDIO" extensions="mp3" extensionsMsgError="Seuls le format MP3 est supporté"/>

</@mainTemplate>