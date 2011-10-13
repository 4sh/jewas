<#include "../common/mainTemplate.ftl">
<#include "common/create-content-header.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Création d'un contenu" selectedMenuItem=""
               stylesheets=["/public/css/bbeeg/create.css"]
               scripts=[chosenJS,
                        "/public/js/jewas/jewas-forms.js",
                        "/public/js/bbeeg/content/create-text.js"]
               useChosen=true>

    <div class="create_center">

        <script id="authorItemTemplate" type="text/x-jquery-tmpl">
            <label class="style_label">Auteur : </label>{{= surname}} {{= name}}
        </script>

        <script id="domainItemTemplate" type="text/x-jquery-tmpl">
            <option value="{{= id}}" {{if selected}} selected {{/if}}> {{= label}} </option>
        </script>

        <script id="tagItemTemplate" type="text/x-jquery-tmpl">
            <option value="{{= tag}}" {{if selected}} selected {{/if}}> {{= tag}} </option>
        </script>

            <script type="text/javascript">
                $(
                    function() {
                        <#if content??>
                            
                            // Load the title
                            $("#title").val("${content.header().title()}");

                            // Load the description
                            $("#description").text("${content.header().description()}");

                            // Load the content
                            $.ajax({
                                    url: "${content.url()}",
                                    success: function (data) {
                                        $("#content").append(data);
                                    }
                            });

                            // Load the domains
                            var domains = [];
                            <#list content.header().domains() as item>
                                domains.push(${item.id()?c});
                            </#list>
                            loadDomains(domains);

                             // Load the tags
                            var tags = [];
                            <#list content.header().tags() as item>
                                tags.push("${item}");
                            </#list>
                            loadTags(tags);
                        <#else>
                            loadDomains([]);
                            loadTags([]);
                        </#if>
                        }
                );
            </script>

        <div id="confirmationDialog" title="Succès">
            <#if content??>
                <p>Votre texte a été modifié avec succès !</p>
            <#else>
                <p>Votre texte a été créé avec succès !</p>
            </#if>
        </div>

        <#if content??>
            <h3>Modification d'un texte</h3>
        <#else>
            <h3>Création d'un texte</h3>
        </#if>

        <form id="createContent" action="<#if content??>/content/${content.header().id()?c}<#else>/content</#if>" method="post">
        <@createContentHeader/>

            <div>
                <div><label for="content" class="style_label">Contenu</label> : </div>
                <div class="create_textarea"><textarea rows="8" cols="91" id="content" required="required"></textarea></div>
            </div>
            <div class="create_buttons"><input type="submit" value="Enregistrer" /></div>
        </form>
    </div>
</@mainTemplate>