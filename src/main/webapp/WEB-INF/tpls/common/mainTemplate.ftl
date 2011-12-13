<#-- Allows to say if you want to use compressed js files or not -->
<#assign compressedJS = "false">

<#macro rootMenuItem id="" additionnalClasses="">
    <div class="menuright_item ${additionnalClasses}" id="${id}"><#nested></div>
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
        useChosen=false>
<!DOCTYPE html>
<html>
<head>
    <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <meta charset="UTF-8">

    <link rel="shortcut icon" type="image/x-icon" href="/public/images/bbeeg/favicon.ico"/>

    <link href='http://fonts.googleapis.com/css?family=Muli:300' rel='stylesheet' type='text/css'>

    <link rel="stylesheet" href="/public/css/bbeeg/bbeeg.css"/>

    <link rel="stylesheet"
          href="/public/css/jquery/jquery-ui-1.8.16.custom.css"/>

    <#if useChosen>
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
    <#--<script type="application/javascript"-->
            <#--src="/public/js/jquery/jquery-1.6.2.js"></script>-->
    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.js"></script>
    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.14/jquery-ui.js"></script>
    <script type="application/javascript"
            src="http://ajax.aspnetcdn.com/ajax/jquery.templates/beta1/jquery.tmpl.js"></script>
</#if>

    <script type="application/javascript" src="/public/js/bbeeg/main.js"></script>

    <#if useChosen>
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
<script id="actionMenuTemplate" type="text/x-jquery-tmpl">
      {{if role != 'student'}}
        <a class="createNewContent menuright_item menuright_action hand_cursor">Créer</a>
        <div class="sepa_menu menuright_item"></div>
        <a class ="menuright_item menuright_action" href="/content/search-user-content.html">Gérer</a>
        <div class="sepa_menu menuright_item "></div>
    {{/if}}
    {{if role == 'administrator'}}
        <a class="menuright_item menuright_action" href="/content/search-content-to-treat.html">Administrer</a>
        <div class="sepa_menu menuright_item"></div>
    {{/if}}
</script>

<script id="userConnectedNameTemplate" type="text/x-jquery-tmpl">
    <span class="hand_cursor">{{= surname}} {{= name}}</span>
</script>
    <div class="page">
        <header>
            <div id="menu">
                <div id="menuleft">
                    <a href="/dashboard/dashboard.html"><img src="/public/images/bbeeg/bbeeg_logo.png" onmouseout="src='/public/images/bbeeg/bbeeg_logo.png'" onmouseover="src='/public/images/bbeeg/bbeeg_logo_hover.png'" alt="Logo BBEEG"/></a>
                </div>
                <div id="menuright">
                    <@rootMenuItem id="input_recherche">
                        <input id="searchInput" type="text">
                    </@rootMenuItem>
                    <@rootMenuItem id="recherche_item">
                        <img id="searchLoop" src="/public/images/bbeeg/loupe.png" alt="Recherche" class="hand_cursor"/>
                    </@rootMenuItem>
                    <@rootMenuItem id="sepa_menu" additionnalClasses="sepa_menu"/>
                    <@rootMenuItem id="userprofile">
                        <div id="connectedUser" class="userprofile"></div>
                        <@subMenu id="profileSubMenu" width="125">
                           <@subMenuItem id="parametersMenuItem" title="Paramètres" />
                           <@subMenuItem id="disconnectMenuItem" title="Déconnexion" />
                       </@subMenu>
                    </@rootMenuItem>
                    <@rootMenuItem id="userprofile_arrow" additionnalClasses="fleche userprofile">
                        <img src="/public/images/bbeeg/fleche.png" alt="Flèche menu déroulant"/>
                    </@rootMenuItem>
                </div>
            </div>
        </header>

        <div style="visibility: hidden">
            <div id="createNewContentDialog" title="Créer un nouveau contenu" class="dialogTitle">
                <div>
                    <span>Type de contenu:</span>
                    <select class="createContentSelect">
                        <option value="/content/text/create.html">Ajouter un contenu texte</option>
                        <option value="/content/document/create.html">Ajouter un contenu PDF</option>
                        <option value="/content/image/create.html">Ajouter un contenu image</option>
                        <option value="/content/video/create.html">Ajouter un contenu vidéo</option>
                        <option value="/content/audio/create.html">Ajouter un contenu audio</option>
                        <option value="/content/eeg/create.html">Ajouter un contenu EEG</option>
                    </select>
                </div>
            </div>
        </div>

        <div class="container">
            <div class="grid">
                <#nested>
            </div>
        </div>

        <footer>
            <a href="#todo">Mentions L&eacute;gales - BB-EEG v${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.appVersion()}</a>
        </footer>
    </div>
</body>
</html>
</#macro>