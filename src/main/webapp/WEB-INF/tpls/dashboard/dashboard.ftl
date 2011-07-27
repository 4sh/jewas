<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Ecran d'accueil"
        selectedMenuItem="dashboard"
        scripts=["/public/js/tabs/tabs.js", "/public/js/bbeeg/dashboard/dashboard.js"]
        stylesheets=["/public/css/tabs/tabs.css"]>
    <script id="contentItemTemplate" type="text/html">
        <div> <p> {{= name}} </p> </div>
    </script>

    <script id="authorItemTemplate" type="text/html">
        <li> {{= name}} </li>
    </script>

    <div id="user-informations">
        <p id="lastConnectionDate"/>

        <div id="dashboard">
            <div class="dashboard-group">
                <p> Mes contenus </p>
                <div id="user-contents"></div>
            </div>
            <div class="dashboard-group">
                <p> Mes cours </p>
                <div id="user-lessons"></div>
            </div>
            <div class="dashboard-group">
                <p> Mes tests </p>
                <div id="user-tests"></div>
            </div>
        </div>
    </div>

    <div id="plateform-informations">
        <p id="totalNumberOfContent"/>
        <div>
            <p> Les derniers auteurs à avoir ajouté du contenu : </p>
            <ul id="lastAuthors">
            </ul>
        </div>
        <div></div>
    </div>

</@mainTemplate>