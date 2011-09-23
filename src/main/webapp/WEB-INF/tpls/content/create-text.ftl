<#include "../common/mainTemplate.ftl">


<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Création d'un contenu" selectedMenuItem=""
               scripts=[chosenJS,
                        "/public/js/jewas/jewas-forms.js",
                        "/public/js/bbeeg/content/create-text.js"]
               useChosen=true>

    <script id="domainItemTemplate" type="text/x-jquery-tmpl">
        <option value="{{= id}}" {{if selected}} selected {{/if}}> {{= label}} </option>
    </script>


        <script type="text/javascript">
            $(
                function() {
                    <#if content??>
                        // Load the author
                        // TODO: load the content author

                        // Load the title
                        $("#title").val("${content.header().title()}");

                        // Load the description
                        $("#description").append("${content.header().description()}");

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
                    <#else>
                        loadDomains([]);
                    </#if>
                    }
            );
        </script>

    <div id="confirmationDialog" title="Succès">
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
    </#if>

    <form id="createContent" action="<#if content??>/content/${content.header().id()?c}<#else>/content</#if>" method="post">
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