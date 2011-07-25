<#macro mainTemplate title scripts>
    <!DOCTYPE html>
    <html>
    <head>
        <link rel="stylesheet" href="public/css/bbeeg.css"/>
        <script type="application/javascript" src="public/js/jquery.js"></script>

        <#list scripts as script>
            <script type="application/javascript" src=${x}></script>
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

        <div>
            <#nested>
        </div>


        <div id="footer"/>
    </body>
    </html>
</#macro>