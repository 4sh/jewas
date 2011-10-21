<#include "../common/mainTemplate.ftl">
<#include "common/create-content-header.ftl">


<@mainTemplate title="Création d'un contenu type EEG" selectedMenuItem="creation"
            scripts=["/public/js/jewas/jewas-forms.js","/public/js/fileUpload/fileuploader.js"]
            stylesheets=["/public/css/fileUpload/fileuploader.css", "/public/css/bbeeg/create.css"]
useChosen=true>

<script type="application/javascript" src="/public/js/bbeeg/content/create-eeg-content.js"></script>
<script type="application/javascript">
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
        $("#cancelBtn").bind('click', function () {
            eegContentCreator.removeUploadedFiles();
            history.go(-1);
        });
    });
</script>

<script id="domainItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= label}} </option>
</script>

<script id="tagItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= tag}}" {{if selected}} selected {{/if}}> {{= tag}} </option>
</script>

<script id="errorMessageTemplate" type="text/x-jquery-tmpl">
    <span>{{= errorMessage}}</span>
</script>

<script id="videoItemTemplate" type="text/x-jquery-tmpl">
    <div class="sepa_horizontal"></div>
   
    <h4>2 - Configuration de la vidéo</h4>
    <div class="video">
        <div class="create_upload">
            <div class="video-uploader">
                <span class="style_label">Sélectionnez votre fichier video :</span>
                <input class="upload-file-info" type="text" style="width:300px;"/>
                <button class="upload" href="#">Parcourir</button>
                <span class="upload-status"></span>
                <br/>
            </div>
        </div>
        <div class='video-conf'>
            <label class="style_label"> Début de la vidéo : </label> <input class="video-start-hours" type="text" style="width: 20px;"/> <span class="style_clock">h</span> <input class="video-start-minutes" type="text" style="width: 20px;"/> <span class="style_clock">min</span> <input class="video-start-seconds" type="text" style="width: 30px;"/> <span class="style_clock">s</span>
            <span class="sepa_element">&mdash;</span><label class="style_label"> Fin de la vidéo : </label> <input class="video-stop-hours" type="text" style="width: 20px;"/> <span class="style_clock">h</span> <input class="video-stop-minutes" type="text" style="width: 20px;"/> <span class="style_clock">min</span> <input class="video-stop-seconds" type="text" style="width: 30px;"/> <span class="style_clock">s</span>
        </div>
        <div class='video-conf-error-banner'></div>
    </div>
</script>

<script id="signalItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= label}} </option>
</script>

<script id="montageItemTemplate" type="text/x-jquery-tmpl">
     <h4>3 - Configuration de l'affichage</h4>
    <div class="bottom_space">
        <label for="zoom" class="style_label"> Niveau de zoom : </label>
        <select id="zoom" style="width:70px;">
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="4">4</option>
            <option value="8">8</option>
            <option value="16">16</option>
        </select>
        <span class="sepa_element">&mdash;</span>
        <label for="frameDuration" class="style_label"> Durée de la fenêtre d'affichage : </label>
        <select id="frameDuration" style="width:70px;">
            <option value="1000">1s</option>
            <option value="5000">5s</option>
            <option value="10000">10s</option>
            <option value="20000" selected="true">20s</option>
            <option value="60000">1m</option>
            <option value="300000">5m</option>
            <option value="600000">10m</option>
        </select>
    </div>
    <div id="montages">
        <div class="montage">
            <h5>Montage</h5>
            <div class="bottom_space">
                 <input id="allSignals" type="checkbox" checked/> <span class="style_clock">Afficher tous les signaux.</span>
            </div>
            <div>
                <div class="style_label bottom_space">Sélectionnez les signaux à afficher :</div>
                <div class="bottom_space"><select class="montage-signalsToDisplay chzn-select side-by-side clearfix" disabled multiple style="width:567px;"></select></div>
            </div>
        </div>
    </div>
</script>

<script id="montageOperationItemTemplate" type="text/x-jquery-tmpl">
     <div class="montage-operation">
        <div class="style_label floatLeft"><div class="signal_label floatLeft"> Signal 1 : </div><select class="montage-operation-s1 chzn-select" style="width:170px;"></select></div>
        <span class="sepa_signal floatLeft">&mdash;</span>
        <div class="floatLeft">
            <select id="addorsub" style="width:45px;" class="montage-operation-operator">
                <option value="+">+</option>
                <option value="-">-</option>
            </select>
        </div>
        <span class="sepa_signal floatLeft">&mdash;</span>
        <div class="style_label floatLeft"><div class="signal_label floatLeft">Signal 2 : </div><select class="montage-operation-s2 chzn-select side-by-side clearfix" style="width:170px;"></select></div>

        <div class="new_signal_line">
            <button class="montage-operation-delete" type="button"> - </button>
            <button class="montage-operation-add"  type="button"> + </button>
        </div>
    </div>
</script>

<div id="confirmationDialog" title="Succès">
    <p>Votre EEG a été créé avec succès !</p>
</div>

<div class="create_center">
    <h3>Création d'un EEG</h3>

    <form id="createContent" action="/content">
    <@createContentHeader/>

    <div class="sepa_horizontal"></div>
    <h4>1 - Configuration de l'EEG</h4>    
        <div class="create_upload">
            <div id="eeg-uploader">
               <span class="style_label">Sélectionnez votre fichier EEG :</span>
                <input class="upload-file-info" type="text" style="width:300px;">
                <button class="upload" href="#">Parcourir</button>
                <span class="upload-status"></span>
            </div>
        </div>

    <label for="eegStartHours" class="style_label"> Début de l'EEG : </label> <input id="eegStartHours" type="text" style="width: 20px;"> <span class="style_clock">h</span> <input id="eegStartMinutes" type="text" style="width: 20px;"/> <span class="style_clock">min</span> <input id="eegStartSeconds" type="text" style="width: 30px;"/> <span class="style_clock">s</span>
    <span class="sepa_element">&mdash;</span>
    <label for="eegStopHours" class="style_label"> Fin de l'EEG : </label> <input id="eegStopHours" type="text" style="width: 20px;"/> <span class="style_clock">h</span> <input id="eegStopMinutes" type="text" style="width: 20px;"/> <span class="style_clock">min</span> <input id="eegStopSeconds" type="text" style="width: 30px;"/> <span class="style_clock">s</span>
    <div class='eegErrorBanner'></div>
    <br />
    <div id="videos"></div>
    <br />
    
    <div class="sepa_horizontal"></div>

    <br />
    <div id="displayConfig"></div>

    <br /><br />
    <div class="create_buttons">
        <button id="previsualizeBtn" type="button"> Prévisualiser </button>
        <input type="submit" value="Enregistrer" />
        <button id="cancelBtn" type="button" >Annuler</button>
    </div>
</form>
</div>
         <br /><br /><br />
    <div id="previsualizationContainer">
    </div>
</@mainTemplate>