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
        <div class="ui-panel-content-text"><a href="/content/{{= id}}/view.html">{{= title}}</a></div>
    </script>

    <script id="authorItemTemplate" type="text/html">
        <li> {{= name}} </li>
    </script>

    <script id="userDomainItemTemplate" type="text/html">
        <li class="inlined-li"> {{= label}} </li>
    </script>


    <div id="userInformations" class="columnleft">
        <div id="dashboard">
            <div id="dashboard_menu">
                <img src="/public/images/bbeeg/cours_disable.png" alt="Cours et tests"/>

                <div id="dashboard_menuright">
                    <img src="/public/images/bbeeg/contenus_active.png" alt="Contenus"/>
                    <img src="/public/images/bbeeg/select_dashboard_menu.png" alt="Selecteur du menu"/>
                </div>
            </div>
            <div class="dashboard-group dashboard-group-linestart">
                <div class="box">
                    <div id="myContentsPanelLastViewed">
                        <h3>Les derniers contenus consultés<img src="/public/images/bbeeg/title_decoration_right.png" alt="Décoration titre"/></h3>
                        <div id="user-contents-last-viewed"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanelLastAdded">
                        <h3>Les derniers contenus ajoutés<img src="/public/images/bbeeg/title_decoration_right.png" alt="Décoration titre"/></h3>
                        <div id="user-contents-last-added"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group dashboard-group-linestart">
                <div class="box">
                    <div id="myContentsPanelPopulars">
                    <h3>Les contenus les populaires<img src="/public/images/bbeeg/title_decoration_right.png" alt="Décoration titre"/></h3>
                        <div id="user-contents-populars"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanelAdvisable">
                        <h3>Les contenus conseillés<img src="/public/images/bbeeg/title_decoration_right.png" alt="Décoration titre"/></h3>
                        <div id="user-contents-advisable">
                            <p style="margin-top: 0px;">Non disponible l1</p>
                            <p>A DYNAMISER !!!! </p>
                            <p>Non disponible l2</p>
                            <p>Non disponible l3</p>
                            <p>Non disponible l4</p>
                            <p style="margin-bottom: 0px">Non disponible l5</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="generalInformations" class="columnright">
        <div id="columnrighttop">
            <div class="box">
                <div id="user_space" class="panel">
                    <img src="/public/images/bbeeg/title_mon_espace.png" class="title" alt="Mon Espace"/>
                    <div class="texts_columnright">
                        <p>kghsdfjklghdflgbjdfljgkdfbj</p>
                        <p>kghsdfjklghdflgbjdfljgkdfbj</p>
                        <p>kghsdfjklghdflgbjdfljgkdfbj</p>
                        <p>kghsdfjklghdflgbjdfljgkdfbj</p>
                    </div>
                </div>
            </div>
            <div class="box">
                <div id="plateformInformationsPanel" class="panel">
                    <img src="/public/images/bbeeg/title_informations_generales.png" class="title" alt="Informations Générales"/>
                    <div id="plateformInformations">
                        <div class="texts_columnright">
                            <p id="totalNumberOfContent"/>
                            <p> Les derniers auteurs à avoir ajouté du contenu : </p>
                            <ul id="lastAuthors">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="columnrightbottom">
            <div class="box">
                <div id="domainsPanel" class="panel">
                    <img src="/public/images/bbeeg/title_tags.png" class="title" alt="Tags"/>
                    <div id="domainCloud"></div>
                </div>
            </div>
        </div>
    </div>
    <!-- OLD HTML used before CSS refactoring by Alex

            <div class="dashboard-group-fullline dashboard-group-linestart">
                <div class="box">
                    <div id="myDomainsPanel" class="panel">
                        <h3> Mes Domaines </h3>
                        <ul id="user-domains"></ul>
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


        <div class="box">
            <div id="domainsPanel" class="panel">
                <h3> Les domaines </h3>
                <div id="domainCloud"></div>
            </div>
        </div>

    -->

</@mainTemplate>