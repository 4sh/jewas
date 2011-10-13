<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Profil utilisateur"
        selectedMenuItem="profile"
        scripts=["/public/js/bbeeg/user/profile.js"]
        stylesheets=[]>
 <div class="center_profil">
    <div class="login_panel">
        <p id="lastConnectionDate" class='style_clock mini_clock'></p>
        <br />
        <form>
            <div class="line_login">
               <div class="style_label label_login">Nom :</div>
               <div class="input_login">
                   <input id="userName" type="text" style="width: 215px;"/>
               </div>
            </div>
            <br />
            <div class="line_login">
               <div class="style_label label_login">Pr&eacute;nom :</div>
               <div class="input_login">
                   <input id="userSurname" type="text" style="width: 215px;"/>
               </div>
            </div>
            <br />
            <div class="line_login">
               <div class="style_label label_login bottom_space">Courriel :</div>
               <div class="input_login">
                   <input id="userEmail" type="text" style="width: 215px;"/>
               </div>
            </div>

            <div class="button_login">
                <button type="button"> Enregistrer </button>
            </div>

        </form>
    </div>
 </div>
</@mainTemplate>