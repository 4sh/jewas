<#include "../common/mainTemplate.ftl">
<#include "common/create-content-header.ftl">


<@mainTemplate title="Création d'un contenu type EEG" selectedMenuItem="creation"
               scripts=["/public/js/jewas/jewas-forms.js",
                        "/public/js/fileUpload/fileuploader.js"]
               stylesheets=["/public/css/fileUpload/fileuploader.css"] useChosen=true>

    <script type="application/javascript" src="/public/js/bbeeg/content/create-eeg-content.js"></script>
    <script type="text/javascript">
        $( function() {
            new EegContentCreator("eeg-uploader", "video-uploader");
        });
    </script>

    <script id="domainItemTemplate" type="text/x-jquery-tmpl">
        <option value="{{= id}}"> {{= label}} </option>
    </script>


    <div id="confirmationDialog" title="Succès">
        <p>Votre contenu a été créé avec succès !</p>
    </div>

    <h3>Création d'un contenu</h3>

    <form id="createContent" action="/content">
        <@createContentHeader/>

        <p>
            <div id="eeg-uploader">
                <span href="#" class="upload">Selectionner votre fichier Eeg: </span>
                <span class="upload-status"></span>
            </div>
        </p>

        <p>
            <div id="video-uploader">
                <span href="#" class="upload">Selectionner votre fichier video : </span>
                <span class="upload-status"></span>
            </div>
        </p>

        <p><label for="eegStart"> Début de l'EEG </label> <input id="eegStart" type="text"></p>
        <p><label for="eegStop"> Fin de l'EEG </label> <input id="eegStop" type="text"></p>
        <p><label for="zoom"> Niveau de zoom </label>
            <select id="zoom">
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="4">4</option>
                <option value="8">8</option>
                <option value="16">16</option>
            </select>
        </p>
        <p><label for="frameDuration"> Durée de la fenêtre d'affichage </label>
            <select id="frameDuration">
                <option value="1000">1s</option>
                <option value="5000">5s</option>
                <option value="10000">10s</option>
                <option value="20000">20s</option>
                <option value="60000">1m</option>
                <option value="300000">5m</option>
                <option value="600000">10m</option>
            </select>
        </p>

        <p><input type="submit" value="Enregistrer" /></p>
    </form>

</@mainTemplate>