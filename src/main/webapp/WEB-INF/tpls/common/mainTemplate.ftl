<#assign compressedJS = "false">

<#macro mainTemplate title="" selectedMenuItem="dashboard" scripts=[] stylesheets=[]>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <link rel="stylesheet" href="/public/css/bbeeg.css"/>
    <link rel="stylesheet"
          href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/themes/ui-lightness/jquery-ui.css"/>

<#list stylesheets as stylesheet>
    <link rel="stylesheet" href=${stylesheet}/>
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
    <div id="header">
        <ul id="mainMenu" class="inlined-left-group">
            <#if selectedMenuItem == "dashboard">
                <li id="dashboardMenuItem" class="inlined-block menu-item-selected">Accueil</li>
            <#else>
                <li id="dashboardMenuItem" class="inlined-block">Accueil</li>
            </#if>

            <#if selectedMenuItem == "search">
                <li id="searchMenuItem" class="inlined-block menu-item-selected">Recherche</li>
            <#else>
                <li id="searchMenuItem" class="inlined-block">Recherche</li>
            </#if>
        </ul>

        <ul class="inlined-right-group">
            <li class="inlined-block">Profil</li>
        </ul>
    </div>

    <div id="mainContent">
        <#nested>
    </div>

    <div id="footer">Ici un pied de page</div>
</body>
</html>
</#macro>