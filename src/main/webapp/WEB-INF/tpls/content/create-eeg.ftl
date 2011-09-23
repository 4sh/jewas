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

        <p><input type="submit" value="Enregistrer" /></p>
    </form>

</@mainTemplate>