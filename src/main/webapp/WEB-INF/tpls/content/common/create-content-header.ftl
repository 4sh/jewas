<#macro createContentHeader>

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

</#macro>