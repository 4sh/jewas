<#-- Allows to say if you want to use compressed js files or not -->
<#assign compressedJS = "false">

<#macro rootMenuItem title="" id="" selected="false">
    <#if selected == "true">
        <li id="${id}" class="inlined-block menu-item-selected"> <span class="menu-item-title">${title}</span> <span class="selection-indicator"/><#nested></li>
    <#else>
        <li id="${id}" class="inlined-block"><span class="menu-item-title">${title}</span><#nested></li>
    </#if>
</#macro>

<#macro subMenu id="" width="125">
    <div id="${id}" class="submenu-container">
        <ul class="submenu" style="width: ${width}px;">
            <#nested>
        </ul>
    </div>
</#macro>

<#macro subMenuItem id="" title="">
    <li id="${id}"><span>${title}</span></li>
</#macro>

<#macro mainTemplate title="" selectedMenuItem="dashboard"
        scripts=[] stylesheets=[]
        useChosenJS=false useChosenCSS=false>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <link rel="stylesheet" href="/public/css/bbeeg/bbeeg.css"/>

    <link rel="stylesheet"
          href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/themes/ui-lightness/jquery-ui.css"/>

    <#if useChosenCSS>
        <link rel="stylesheet" href="/public/css/chosen/chosen.css"/>
        <#-- Overriding particular chosen classes for bbeeg look and feel -->
        <link rel="stylesheet" href="/public/css/chosen/chosen-bbeeg.css"/>
    </#if>

<#list stylesheets as stylesheet>
    <link rel="stylesheet" href="${stylesheet}" />
</#list>

<#if compressedJS == "true">
    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/jquery-ui.min.js"></script>
    <script type="application/javascript"
            src="http://ajax.aspnetcdn.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js"></script>
<#else>
    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.js"></script>
    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/jquery-ui.js"></script>
    <script type="application/javascript"
            src="http://ajax.aspnetcdn.com/ajax/jquery.templates/beta1/jquery.tmpl.js"></script>
</#if>

    <script type="application/javascript" src="/public/js/bbeeg/main.js"></script>

    <#if useChosenJS>
        <#if compressedJS == "true">
            <script type="application/javascript" src="/public/js/chosen/chosen.jquery.min.js"></script>
        <#else>
            <script type="application/javascript" src="/public/js/chosen/chosen.jquery.js"></script>
        </#if>
    </#if>

<#list scripts as script>
    <script type="application/javascript" src="${script}"></script>
</#list>

    <title>${title}</title>
</head>
<body>
    <div id="container">
        <div id="header">
            <ul id="applicationMenu" class="inlined-left-group">
                <#if selectedMenuItem == "dashboard">
                    <@rootMenuItem id="dashboardMenuItem" title="Accueil" selected="true"/>
                <#else>
                    <@rootMenuItem id="dashboardMenuItem" title="Accueil" selected="false"/>
                </#if>

                <#if selectedMenuItem == "search">
                    <@rootMenuItem id="searchMenuItem" title="Recherche" selected="true"/>
                <#else>
                    <@rootMenuItem id="searchMenuItem" title="Recherche" selected="false"/>
                </#if>
                <#if selectedMenuItem == "creation">
                    <@rootMenuItem id="creationMenuItem" title="Création" selected="true">
                        <@subMenu id="creationSubMenu" width="125">
                            <@subMenuItem id="createTextMenuItem" title="Texte" />
                            <@subMenuItem id="createDocumentMenuItem" title="Document" />
                            <@subMenuItem id="createImageMenuItem" title="Image" />
                            <@subMenuItem id="createVideoMenuItem" title="Vidéo" />
                            <@subMenuItem id="createAudioMenuItem" title="Audio" />
                            <@subMenuItem id="createEegMenuItem" title="EEG" />
                        </@subMenu>
                    </@rootMenuItem>
                <#else>
                    <@rootMenuItem id="creationMenuItem" title="Création" selected="false">
                        <@subMenu id="creationSubMenu" width="125">
                            <@subMenuItem id="createTextMenuItem" title="Texte" />
                            <@subMenuItem id="createDocumentMenuItem" title="Document" />
                            <@subMenuItem id="createImageMenuItem" title="Image" />
                            <@subMenuItem id="createVideoMenuItem" title="Vidéo" />
                            <@subMenuItem id="createAudioMenuItem" title="Audio" />
                            <@subMenuItem id="createEegMenuItem" title="EEG" />
                        </@subMenu>
                    </@rootMenuItem>
                 </#if>
            </ul>

            <ul id="configurationMenu" class="inlined-right-group">
                 <#if selectedMenuItem == "administration">
                    <@rootMenuItem id="adminMenuItem" title="Administration" selected="true">
                        <@subMenu id="adminSubMenu" width="125">
                            <@subMenuItem id="createContentMenuItem" title="Créer un contenu" />
                            <@subMenuItem id="manageMyContentsMenuItem" title="Gérer mes contenus" />
                            <@subMenuItem id="adminContentsMenuItem" title="Administrer les contenus" />
                        </@subMenu>
                    </@rootMenuItem>
                <#else>
                    <@rootMenuItem id="adminMenuItem" title="Administration" selected="false">
                        <@subMenu id="adminSubMenu" width="125">
                            <@subMenuItem id="createContentMenuItem" title="Créer un contenu" />
                            <@subMenuItem id="manageMyContentsMenuItem" title="Gérer mes contenus" />
                            <@subMenuItem id="adminContentsMenuItem" title="Administrer les contenus" />
                        </@subMenu>
                    </@rootMenuItem>
                 </#if>

                <#if selectedMenuItem == "profile">
                    <@rootMenuItem id="profileMenuItem" title='${statics["fr.fsh.bbeeg.security.resources.ConnectedUserResource"].instance().userNames()}' selected="true">
                         <@subMenu id="profileSubMenu" width="125">
                            <@subMenuItem id="parametersMenuItem" title="Paramètres" />
                            <@subMenuItem id="disconnectMenuItem" title="Déconnexion" />
                        </@subMenu>
                    </@rootMenuItem>
                <#else>
                    <@rootMenuItem id="profileMenuItem" title='${statics["fr.fsh.bbeeg.security.resources.ConnectedUserResource"].instance().userNames()}' selected="false">
                         <@subMenu id="profileSubMenu" width="125">
                            <@subMenuItem id="parametersMenuItem" title="Paramètres" />
                            <@subMenuItem id="disconnectMenuItem" title="Déconnexion" />
                        </@subMenu>
                    </@rootMenuItem>
                </#if>
            </ul>
        </div>

        <div id="mainContent">
            <#nested>
        </div>
    </div>
    <div id="footer"> Plateforme d'eLearning BB-EEG v${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.appVersion()}</div>
</body>
</html>
</#macro>