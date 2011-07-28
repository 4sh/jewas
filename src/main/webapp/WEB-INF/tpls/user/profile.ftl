<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Profil utilisateur"
        selectedMenuItem="profile"
        scripts=["/public/js/bbeeg/user/profile.js"]
        stylesheets=[]>

    <p id="lastConnectionDate"></p>

    <form>
        <p> Nom <br/><input id="userName" type="text"></input></p>
        <p> Prenom <br/><input id="userSurname" type="text"></input></p>
        <p> Courriel <br/><input id="userEmail" type="email"></input></p>
    </form>

</@mainTemplate>