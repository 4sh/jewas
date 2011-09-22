<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"]
               useChosenCSS=true useChosenJS=true>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="IMAGE" extensions="png|jpg|pjeg|gif" extensionsMsgError="Seuls les formats PNG, JPEG et GIF sont supportés"/>

</@mainTemplate>