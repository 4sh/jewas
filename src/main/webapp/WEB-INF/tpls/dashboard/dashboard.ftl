<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Ecran d'accueil" scripts=["/public/js/tabs/tabs.js", "/public/js/bbeeg/dashboard/dashboard.js"] stylesheets=["/public/css/tabs/tabs.css"]>
    <script id="contentItemTemplate" type="text/html">
        <div> <p> {{= name}} </p> </div>
    </script>

    <div id="user-informations">
        <p id="lastConnectionDate"> Dernière date de connexion : </p>

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
        <p> 345 contenus sont disponibles actuellement sur la plateforme.</p>
        <div>
            <p> Les derniers auteurs à avoir ajouté du contenu : </p>
            <ul>
                <li> Auteur1 </li>
                <li> Auteur2 </li>
                <li> Auteur3 </li>
                <li> Auteur4 </li>
                <li> Auteur5 </li>
            </ul>
        </div>
        <div></div>
    </div>

</@mainTemplate>