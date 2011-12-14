<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Ecran d'accueil"
        selectedMenuItem="dashboard"
        scripts=["/public/js/tabs/tabs.js",
                 "/public/js/jqcloud/jqcloud-0.2.1.js",
                 "/public/js/tagcloud/jquery.tagcloud-0.5.0.js",
                 "/public/js/tinysort/jquery.tinysort.js",
                 "/public/js/panel/ui.panel.js",
                 "/public/js/bbeeg/content/content-helper.js",
                 "/public/js/bbeeg/dashboard/dashboard.js"]
        stylesheets=["/public/css/tabs/tabs.css",
                     "/public/css/jqcloud/jqcloud.css",
                     "/public/css/panel/ui.panel.css",
                     "/public/css/bbeeg/bbeeg.css",
                     "/public/css/bbeeg/dashboard.css"]>
    <script id="contentItemTemplate" type="text/html">
        <div class="mini_tabs"></div>
        <div class="ui-panel-content-text">
            <div class="mini_icon_type {{= contentHelper.getMiniIcon(type)}}"></div>
            <a href="/content/{{= id}}/view.html">{{= title}}</a>
            <div class="mini_clock">
                <img src="/public/images/bbeeg/calendar.png"/>
                {{= creationDate}}  &nbsp;
                <img src="/public/images/bbeeg/author.png"/>
                {{= author.firstName}} {{= author.lastName}}
            </div>
        </div>
    </script>

    <script id="authorItemTemplate" type="text/html">
        <li> {{= firstName}} {{= lastName}} </li>
    </script>

    <script id="tagItemTemplate" type="text/html">
        <li value="{{= weight}}" title="{{= text}}">
            <a href="{{= url}}">{{= text}}</a>
        </li>
     </script>

    <script id="mySpaceTemplate" type="text/html">
        {{if role == 'student'}}
            <span><b>Bienvenue sur le tableau de bord de la plateforme BB-EEG !!</b> </br> Vous pouvez dès à présent lancer une recherche de contenu de formation via le menu ou en utilisant les liens vers les documents présentés ci-contre.</span>
        {{/if}}
        {{if role == 'teacher'}}
            <span>Vous êtes connecté en tant qu'<b>enseignant</b>. Vous pouvez enrichir la plateforme en ajoutant des contenus:</span>
            <ul class="panel_list">
                <li class="createNewContent"><a class="hand_cursor">Créer un nouveau contenu</a></li>
                <li><a href="/content/search-user-content.html">Gérer mes contenus</a></li>
             </ul>
        {{/if}}
        {{if role == 'administrator'}}
            <span>Vous êtes connecté en tant qu'<b>administrateur</b>. Vous devez à ce titre, répondre aux demandes de publication émises par les enseignants en validant ourejetant les contenus proposés.</span>

            <ul class="panel_list">
                <li class="createNewContent"><a class="hand_cursor">Créer un nouveau contenu</a></li>
                <li><a href="/content/search-content-to-treat.html">Administrer les contenus</a></li>
                <li><a>Administrer les domaines</a></li>
            </ul>
        {{/if}}

    </script>

    <div id="userInformations" class="columnleft">
        <div id="dashboard">
            <div id="dashboard_menu">
                <h2>Cours & Tests | Contenus</h2>
                <div id="dashboard_menuright">
                </div>
            </div>
            <div class="dashboard-group dashboard-group-linestart">
                <div class="box">
                    <div id="myContentsPanelLastViewed">
                        <h3>Les derniers contenus consultés</h3>
                        <div id="user-contents-last-viewed"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanelLastAdded">
                        <h3>Les derniers contenus ajoutés</h3>
                        <div id="user-contents-last-added"></div>
                    </div>
                </div>
            </div>

            <div class="dashboard-group dashboard-group-linestart">
                <div class="box">
                    <div id="myContentsPanelPopulars">
                    <h3>Les contenus les populaires</h3>
                        <div id="user-contents-populars"></div>
                    </div>
                </div>
            </div>
            <div class="dashboard-group">
                <div class="box">
                    <div id="myContentsPanelAdvisable">
                        <h3>Les contenus conseillés</h3>
                         <div id="user-contents-advisable">
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
                    <div class="title_tab"></div>
                    <div class="title_panel">
                        <h3>Mon espace</h3>
                    </div>
                    <div id="mySpace" class="texts_columnright">
                    </div>
                </div>
            </div>

            <div class="box_info">
                <div id="plateformInformationsPanel" class="panel">
                    <div class="title_tab"></div>
                    <div class="title_panel">
                        <h3>Informations générales</h3>
                    </div>
                    <div id="plateformInformations">
                        <div class="texts_columnright">
                            <p id="totalNumberOfContent"></p>
                            <p> Les derniers auteurs à avoir ajouté du contenu : </p>
                            <ul id="lastAuthors" class="panel_list">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="columnrightbottom">
            <div class="box">
                <div id="domainsPanel" class="panel">
                    <div class="title_tab"></div>
                    <div class="title_panel">
                        <h3>Tags</h3>
                    </div>
                    <div id="tagCloud">
                        <ul id="tagList"></ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@mainTemplate>