<#include "create-content-header.ftl">

<#function isInitialContent content="null">
    <#return content == "null">
</#function>

<#macro createContent url type extensions extensionsMsgError createPrevisualizationObject>

<link rel="stylesheet" href="/public/css/bbeeg/create.css" >
<script type="application/javascript" src="/public/js/bbeeg/content/create-content.js"></script>
<script type="application/javascript" src="/public/js/bbeeg/content/content-helper.js"></script>
<script type="application/javascript" src="/public/js/bbeeg/content/content-status.js"></script>

<script type="application/javascript">
    $(function() {

        var contentCreator = new ContentCreator("${type}",
                "${extensions}",
                "${extensionsMsgError}",
                "previsualizationContainer",
                ${createPrevisualizationObject},
                $("#saveBtn"), 
                <#if isInitialContent(content)>
                    true
                <#else>
                    false
                </#if>);


        $(document).bind('videoEncodingError', function() {
            contentCreator.setPostProcess(true);
        });

         /* Mandatory fields handler registration */
        $("#title, #description").change(function() {
            contentCreator.refreshSubmitButton();
        });

        $("#cancelBtn").bind('click', function () {
            contentCreator.removeUploadedFiles();
            history.go(-1);
        });

        var domains = [];
        var tags = [];
        <#if content??>
            // Load the title
            $("#title").val("${content.header().title()?js_string}");
            // Load the description
            $("#description").text("${content.header().description()?js_string}");
            // Load the content
            var url = "${content.url()}";
            var child = ${createPrevisualizationObject}(url);
            $("#previsualizationContainer").empty().append(child);
            // Load the domains
            <#list content.header().domains() as item>
                domains.push(${item.id()?c});
            </#list>
            // Load the tags
            <#list content.header().tags() as item>
                tags.push("${item}");
            </#list>
        </#if>
        contentHelper.loadDomains($("#domains"), $("#domainItemTemplate"), domains);
        contentHelper.loadTags($("#tags"), $("#tagItemTemplate"), tags);
        $("#confirmationDialog").append("<p>" + contentHelper.getSaveConfirmationMessage(<#if content??>"${content.header().status()}"<#else>null</#if>) + "</p>");
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

    <form id="createContent" action="<#if content??>/content/${content.header().id()?c}<#else>${url}</#if>" method="post">
        <@createContentHeader/>

        <div class="sepa_horizontal" style="width: 635px;"></div>

        <div class="create_upload">
            <div id="file-uploader">
                <span class="style_label">Sélectionnez votre fichier : </span>
                <input id="upload-file-info" type="text" style="width:300px;" <#if isInitialContent(content)>required</#if>>
                <button id="upload" href="#">Parcourir</button>
            </div>
            <span id="upstatus"></span>
        </div>

        <div class="create_buttons bottom_space">
            <button id="saveBtn" type="button" <#if isInitialContent(content)>disabled</#if>>Enregistrer</button>
            <button id="cancelBtn" type="button" >Annuler</button>
        </div>

        <div id="previsualizationContainer" class="previewCenter">
        </div>
    </form>

    <div id="confirmationDialog" title="Confirmation">
    </div>

</#macro>