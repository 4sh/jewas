<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Profil utilisateur"
        selectedMenuItem="profile"
        scripts=["/public/js/jewas/jewas-forms.js",
                 "/public/js/bbeeg/user/profile.js"]
        stylesheets=[]>
 <div class="center_profil">
     <div id="successDialog" title="Succès">
        <p>Les informations ont été mises à jour avec succès.</p>
     </div>
     <div id="failureDialog" title="Erreur">
        <p>Une erreur est survenue lors de la mise à jour des informations.</p>
        <p>Si l'erreur persiste, veuillez contacter l'administrateur du système.</p>
     </div>
     <div class="login_panel">
        <p id="lastConnectionDate" class='style_clock mini_clock'></p>
        <br />
        <form id="userProfileForm">
            <div class="line_login">
               <div class="style_label label_login">Identifiant :</div>
               <div class="input_login">
                   <input id="login" type="text" style="width: 215px;" disabled/>
               </div>
            </div>
            <br />
            <div class="line_login">
               <div class="style_label label_login">Nom :</div>
               <div class="input_login">
                   <input id="lastName" type="text" style="width: 215px;"/>
               </div>
            </div>
            <br />
            <div class="line_login">
               <div class="style_label label_login">Pr&eacute;nom :</div>
               <div class="input_login">
                   <input id="firstName" type="text" style="width: 215px;"/>
               </div>
            </div>
            <br />
            <div class="line_login">
               <div class="style_label label_login bottom_space">Email :</div>
               <div class="input_login">
                   <input id="email" type="text" style="width: 215px;"/>
               </div>
            </div>

            <div class="button_login">
                <input type="submit" value="Enregistrer" />
            </div>
        </form>
    </div>
 </div>
</@mainTemplate>