<#include "../common/mainTemplate.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=[chosenJS,
                        "/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/chosen/chosen.css",
                            "/public/css/fileUpload/fileuploader.css"]>

    <#include "common/create-content.ftl">
    <@createContent url="/content" type="DOCUMENT" extensions="pdf" extensionsMsgError="Seuls le format PDF est supporté"/>

</@mainTemplate>