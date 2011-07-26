<#assign compressedJS = "true">

<#macro mainTemplate title scripts stylesheets>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="public/css/bbeeg.css"/>
    <#list stylesheets as stylesheet>
        <link rel="stylesheet" href=${stylesheet}/>
    </#list>

    <#if compressedJS == "true">
        <script type="application/javascript"
                src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
        <#else>
            <script type="application/javascript"
                    src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.js"></script>
    </#if>
    <!-- FIXME : use min jquery template -->
    <script type="application/javascript" src="public/js/jquery/jquery.tmpl.js"></script>
    <#list scripts as script>
        <script type="application/javascript" src=${script}></script>
    </#list>

    <title>${title}</title>
</head>
<body>
<div id="header">
    <ul class="inlinedLeftGroup">
        <li class="inlinedBlock">Accueil</li>
        <li class="inlinedBlock">Recherche</li>
    </ul>

    <ul class="inlinedRightGroup">
        <li class="inlinedBlock">Profil</li>
    </ul>
</div>

<div id="mainContent">
    <#nested>
</div>


<div id="footer">Ici un pied de page</div>
</body>
</html>
</#macro>