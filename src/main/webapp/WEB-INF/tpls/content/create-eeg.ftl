<#include "../common/mainTemplate.ftl">
<#include "common/create-content-header.ftl">


<@mainTemplate title="Création d'un contenu type EEG" selectedMenuItem="creation"
scripts=["/public/js/jewas/jewas-forms.js",
"/public/js/fileUpload/fileuploader.js"]
stylesheets=["/public/css/fileUpload/fileuploader.css"] useChosen=true>

<script type="application/javascript" src="/public/js/bbeeg/content/create-eeg-content.js"></script>
<script type="text/javascript">
    var eegContentCreator;
    $( function() {
        eegContentCreator = new EegContentCreator(
                "eeg-uploader",
                {
                    previsualizeButtonId : "previsualizeBtn",
                    previsualizationContainerId : "previsualizationContainer",
                    previsualizationUrl : "${statics["fr.fsh.bbeeg.common.config.BBEEGConfiguration"].INSTANCE.cliOptions().visioRootUrl()}"
                }
        );
        eegContentCreator.addMontage($('#montages'));
        eegContentCreator.addVideo($('#videos'));

        $("#cancelBtn").bind('click', function () {
            eegContentCreator.removeUploadedFiles();
            history.go(-1);
        });
    });
</script>

<script id="domainItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= label}} </option>
</script>

<script id="videoItemTemplate" type="text/x-jquery-tmpl">
    <div class="video-uploader">
        Sélectionnez votre fichier video :
        <input class="upload-file-info" type="text">
        <button class="upload" href="#">Parcourir</button>
        <span class="upload-status"></span>

        <label> Début de la vidéo (en ms) </label> <input class="video-start" type="text">
        <label> Fin de la vidéo (en ms) </label> <input class="video-stop" type="text">
    </div>
</script>

<script id="montageItemTemplate" type="text/x-jquery-tmpl">
    <div class="montage">
        <h3>Montage</h3>
        <p>
            <label> Entrez les numéros des signaux (0-based) que vous souhaitez afficher (séparés par une virgule)</label>
            <input class="montage-signalsToDisplay" type="text"/>
        </p>
    </div>
</script>

<script id="montageOperationItemTemplate" type="text/x-jquery-tmpl">
    <p class="montage-operation">
        Signal 1 <input class="montage-operation-s1" type="text"/>
        Operateur (ADD ou SUB) <input class="montage-operation-operator" type="text"/>
        Signal 2 <input class="montage-operation-s2" type="text"/>

        <button class="montage-operation-delete" type="button"> - </button>
        <button class="montage-operation-add"  type="button"> + </button>
    </p>
</script>

<div id="confirmationDialog" title="Succès">
    <p>Votre contenu a été créé avec succès !</p>
</div>

<h3>Création d'un contenu</h3>

<form id="createContent" action="/content">
<@createContentHeader/>

    <br />

    <p>
        <div id="eeg-uploader">
            Sélectionnez votre fichier Eeg :
            <input class="upload-file-info" type="text">
            <button class="upload" href="#">Parcourir</button>
            <span class="upload-status"></span>
        </div>
    </p>

    <br />

    <h4>Configuration de l'EEG</h4>

    <p>
        <label for="eegStart"> Début de l'EEG (en s) </label> <input id="eegStart" type="text">
        <label for="eegStop"> Fin de l'EEG (en s) </label> <input id="eegStop" type="text">
    </p>
    <br />
    <div id="videos"></div>
    <br />
    <p>
        <label for="zoom"> Niveau de zoom </label>
        <select id="zoom">
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="4">4</option>
            <option value="8">8</option>
            <option value="16">16</option>
        </select>
        <label for="frameDuration"> Durée de la fenêtre d'affichage </label>
        <select id="frameDuration">
            <option value="1000">1s</option>
            <option value="5000">5s</option>
            <option value="10000">10s</option>
            <option value="20000" selected="true">20s</option>
            <option value="60000">1m</option>
            <option value="300000">5m</option>
            <option value="600000">10m</option>
        </select>
    </p>

    <br />
    <div id="montages"></div>

    <br />

    <p> Si vous souhaitez prévisualiser l'EEG avec votre configuration <button id="previsualizeBtn" type="button"> cliquez ici </button></p>
    <div id="previsualizationContainer">

    </div>

    <br />
    <p>
        <input type="submit" value="Enregistrer" />
        <button id="cancelBtn" type="button" >Annuler</button>
    </p>
</form>

</@mainTemplate>