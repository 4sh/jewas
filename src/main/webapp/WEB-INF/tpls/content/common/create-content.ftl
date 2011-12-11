<#include "create-content-header.ftl">

<#macro createContent url type extensions extensionsMsgError createPrevisualizationObject>

<link rel="stylesheet" href="/public/css/bbeeg/create.css" >
<script type="application/javascript" src="/public/js/bbeeg/content/create-content.js"></script>
<script type="application/javascript">
    $( function() {
        var contentCreator = new ContentCreator("${type}", "${extensions}", "${extensionsMsgError}", "previsualizationContainer", ${createPrevisualizationObject});

        $("#cancelBtn").bind('click', function () {
            contentCreator.removeUploadedFiles();
            history.go(-1);
        });

        <#if content??>

            // Load the title
            $("#title").val("${content.header().title()}");

            // Load the description
            $("#description").append("${content.header().description()}");

            // Load the content
            var url = "${content.url()}";
            var child = ${createPrevisualizationObject}(url);
            $("#previsualizationContainer").empty().append(child);

            // Load the domains
            var domains = [];
            <#list content.header().domains() as item>
                domains.push(${item.id()?c});
            </#list>
            contentCreator.loadDomains(domains);

            // Load the tags
            var tags = [];
            <#list content.header().tags() as item>
                tags.push("${item}");
            </#list>
            contentCreator.loadTags(tags)
        <#else>
            contentCreator.loadDomains([]);
            contentCreator.loadTags([]);
        </#if>
    });
</script>
<script id="authorItemTemplate" type="text/x-jquery-tmpl">
    <label class="style_label">Auteur : </label>{{= surname}} {{= name}}
</script>
<script id="domainItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}" {{if selected}} selected {{/if}}> {{= label}} </option>
</script>
<script id="tagItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= tag}}" {{if selected}} selected {{/if}}> {{= tag}} </option>
</script>

<#--    <div id="confirmationDialog" title="Succès">
        <#if content??>
            <p>Votre contenu a été modifié avec succès !</p>
        <#else>
            <p>Votre contenu a été créé avec succès !</p>
        </#if>
    </div>

    <#if content??>
        <h3>Modification d'un contenu</h3>
    <#else>
        <h3>Création d'un contenu</h3>
    </#if>-->

    <form id="createContent" action="<#if content??>/content/${content.header().id()?c}<#else>${url}</#if>" method="post">
        <@createContentHeader/>

        <div class="sepa_horizontal" style="width: 635px;"></div>

        <div class="create_upload">
            <div id="file-uploader">
                <span class="style_label">Sélectionnez votre fichier : </span>
                <input id="upload-file-info" type="text" style="width:300px;">
                <button id="upload" href="#">Parcourir</button>
            </div>
            <span id="upstatus"></span>
        </div>

        <div class="create_buttons bottom_space">
            <input type="submit" value="Enregistrer" />
            <button id="cancelBtn" type="button" >Annuler</button>
        </div>

        <div id="previsualizationContainer" class="previewCenter">
        </div>
    </form>
</#macro>