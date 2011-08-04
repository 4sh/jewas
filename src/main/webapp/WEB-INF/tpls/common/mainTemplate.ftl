<#assign compressedJS = "false">

<#macro menuItem title="" id="" selected="false">
    <#if selected == "true">
        <li id="${id}" class="inlined-block menu-item-selected"> <span class="menu-item-title">${title}</span> <span class="selection-indicator"/></li>
    <#else>
        <li id="${id}" class="inlined-block"><span class="menu-item-title">${title}</span></li>
    </#if>
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
                    <@menuItem id="dashboardMenuItem" title="Accueil" selected="true"/>
                <#else>
                    <@menuItem id="dashboardMenuItem" title="Accueil" selected="false"/>
                </#if>

                <#if selectedMenuItem == "search">
                    <@menuItem id="searchMenuItem" title="Recherche" selected="true"/>
                <#else>
                    <@menuItem id="searchMenuItem" title="Recherche" selected="false"/>
                </#if>
            </ul>

            <ul id="configurationMenu" class="inlined-right-group">
                <#if selectedMenuItem == "profile">
                    <@menuItem id="profileMenuItem" title="Profil" selected="true"/>
                <#else>
                    <@menuItem id="profileMenuItem" title="Profil" selected="false"/>
                </#if>
            </ul>
        </div>

        <div id="mainContent">
            <#nested>
        </div>
    </div>
    <div id="footer"> Plateforme d'eLearning BB-EEG </div>
</body>
</html>
</#macro>