<#include "../common/mainTemplate.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Création d'un contenu" selectedMenuItem="creation"
               scripts=[chosenJS,
                        "/public/js/jewas/jewas-forms.js",
                        "/public/js/bbeeg/content/create-text.js"]
               stylesheets=["/public/css/chosen/chosen.css"]>

    <script id="domainItemTemplate" type="text/x-jquery-tmpl">
        <option value="{{= id}}"> {{= label}} </option>
    </script>


    <div id="confirmationDialog" title="Succès">
        <p>Votre contenu a été créé avec succès !</p>
    </div>

    <h3>Création d'un contenu</h3>

    <form id="createContent" action="/content" method="post">
        <p>Auteur : toto</p>
        <p><label for="title">Titre</label> : <input type="text" id="title" name="title" /></p>
        <p><label for="description">Description</label> :<br/><textarea rows="3" cols="100" id="description" name="description"></textarea></p>
        <div class="criteria-line">
            <div class="criteria-label"><label for="domains">Domaines</label> :</div>
            <div class="criteria-field">
                <select id="domains" name="domains" class="chzn-select side-by-side clearfix" multiple>
                </select>
            </div>
        </div>
        <p><label for="content">Contenu</label> :<br/><textarea rows="8" cols="100" id="content"></textarea></p>
        <p><input type="submit" value="Enregistrer" /></p>
    </form>

</@mainTemplate>