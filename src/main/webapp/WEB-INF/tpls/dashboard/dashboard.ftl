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
        <div class="ui-panel-content-text">{{= title}}</div>
    </script>

    <script id="authorItemTemplate" type="text/html">
        <li> {{= name}} </li>
    </script>

    <script id="userDomainItemTemplate" type="text/html">
        <li class="inlined-li"> {{= label}} </li>
    </script>


    <div id="userInformations">
        <div id="dashboard">
            <div class="dashboard-group-fullline dashboard-group-linestart">
                <div class="box">
                    <div id="myDomainsPanel" class="panel">
                        <h3> Mes Domaines </h3>
                        <ul id="user-domains"></ul>
                    </div>
                </div>
            </div>
            <div class="dashboard-group dashboard-group-linestart">
                <div class="box">
                    <div id="myContentsPanelLastAdded" class="panel">
                        <h3> Contenus ajoutés </h3>
                        <div id="user-contents-last-added"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanelLastViewed" class="panel">
                        <h3> Contenus vus </h3>
                        <div id="user-contents-last-viewed"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanelPopulars" class="panel">
                        <h3> Contenus populaires </h3>
                        <div id="user-contents-populars"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group-fullline dashboard-group-linestart">
                <div class="box">
                    <div id="myLessonsPanel" class="panel">
                        <h3> Mes cours </h3>
                        <div id="user-lessons">
                            <#--
                            Hardcoded styles here since it is a temporary style
                            JQuery is adding extra div tag when using panel(), this div is
                            made of class="ui-panel-content-text" which adds extra margin to elements
                            In real life, we would use an ajax query which would replace the <div> tag
                            and thus, extra style shouldn't be needed anymore...
                            -->
                            <p style="margin-top: 0px;">Non disponible l1</p>
                            <p>Non disponible l2</p>
                            <p>Non disponible l3</p>
                            <p>Non disponible l4</p>
                            <p style="margin-bottom: 0px">Non disponible l5</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group-fullline dashboard-group-linestart">
                <div class="box">
                    <div id="myTestsPanel" class="panel">
                        <h3> Mes tests </h3>
                        <div id="user-tests">
                            <p style="margin-top: 0px;">Non disponible l1</p>
                            <p>Non disponible l2</p>
                            <p>Non disponible l3</p>
                            <p>Non disponible l4</p>
                            <p style="margin-bottom: 0px;">Non disponible l5</p>
                        </div>
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