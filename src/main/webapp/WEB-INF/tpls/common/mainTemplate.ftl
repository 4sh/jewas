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

<#macro mainTemplate title="" selectedMenuItem="dashboard" scripts=[] stylesheets=[]>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <link rel="stylesheet" href="/public/css/bbeeg/bbeeg.css"/>
    <link rel="stylesheet"
          href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/themes/ui-lightness/jquery-ui.css"/>

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
            </ul>

            <ul id="configurationMenu" class="inlined-right-group">
                <@rootMenuItem id="adminMenuItem" title="Administration" selected="false">
                    <@subMenu id="adminSubMenu" width="125">
                        <@subMenuItem id="createContentMenuItem" title="Créer un contenu" />
                    </@subMenu>
                </@rootMenuItem>
                <#if selectedMenuItem == "profile">
                    <@rootMenuItem id="profileMenuItem" title="Profil" selected="true"/>
                <#else>
                    <@rootMenuItem id="profileMenuItem" title="Profil" selected="false"/>
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