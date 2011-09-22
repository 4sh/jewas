<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosen=true>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="VIDEO" extensions="mp4" extensionsMsgError="Seuls le format MP4 est supporté"/>

</@mainTemplate>