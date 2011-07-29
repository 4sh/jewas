<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Ecran d'accueil"
        selectedMenuItem="dashboard"
        scripts=["/public/js/tabs/tabs.js",
                 "/public/js/jqcloud/jqcloud-0.2.1.js",
                 "/public/js/panel/ui.panel.js",
                 "/public/js/bbeeg/dashboard/dashboard.js"]
        stylesheets=["/public/css/tabs/tabs.css",
                     "/public/css/jqcloud/jqcloud.css",
                     "/public/css/panel/ui.panel.css",
                     "/public/css/bbeeg/dashboard.css"]>
    <script id="contentItemTemplate" type="text/html">
        <div> <p> {{= title}} </p> </div>
    </script>

    <script id="authorItemTemplate" type="text/html">
        <li> {{= name}} </li>
    </script>

    <div id="userInformations">
        <p id="lastConnectionDate"/>

        <div id="dashboard">
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanel" class="panel">
                        <h3> Mes contenus </h3>
                        <div id="user-contents"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myLessonsPanel" class="panel">
                        <h3> Mes cours </h3>
                        <div id="user-lessons"> Non disponible </div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myTestsPanel" class="panel">
                        <h3> Mes tests </h3>
                        <div id="user-tests"> Non disponible </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="generalInformations">
        <div class="box">
            <div id="plateformInformationsPanel" class="panel">
                <h3> Informations générales </h3>
                <div id="plateformInformations">
                    <p id="totalNumberOfContent"/>
                    <div>
                        <p> Les derniers auteurs à avoir ajouté du contenu : </p>
                        <ul id="lastAuthors">
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="box">
            <div id="domainsPanel" class="panel">
                <h3> Les domaines </h3>
                <div id="domainCloud"></div>
            </div>
        </div>
    </div>


</@mainTemplate>