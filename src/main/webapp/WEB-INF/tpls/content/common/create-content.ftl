<#macro createContent url type extensions extensionsMsgError>

<script type="application/javascript" src="/public/js/bbeeg/content/create-content.js"></script>
<script type="text/javascript">
    $( function() {
        new ContentCreator("${type}", "${extensions}", "${extensionsMsgError}");
    });
</script>

    <script id="domainItemTemplate" type="text/x-jquery-tmpl">
        <option value="{{= id}}"> {{= label}} </option>
    </script>


    <div id="confirmationDialog" title="Succès">
        <p>Votre contenu a été créé avec succès !</p>
    </div>

    <h3>Création d'un contenu</h3>

    <form id="createContent" action="${url}" method="post">
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
        <p>
            <div id="file-uploader">
                <span id="upload" href="#" class="          ">Selectionner votre fichier : </span>
                <span id="upstatus"></span>
                <ul id="media"></ul>
            </div>
        </p>
        <p><input type="submit" value="Enregistrer" /></p>
    </form>

</#macro>