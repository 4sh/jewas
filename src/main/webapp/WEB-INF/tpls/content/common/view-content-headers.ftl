<#macro viewContentHeaders content>
    <script type="text/javascript">
        $(
            function() {
                $("#domains").chosen();
            }
        );
    </script>

    <p>Auteur : ${content.header().author().name()}</p>
    <p>Intitulé : ${content.header().title()}</p>
    <p>Domaine(s) :
        <#list content.header().domains() as item>
            ${item.label()}<#if item_has_next>, </#if>
        </#list>
    </p>
    <p>Description : </p> <textarea rows="3" cols="100" readonly="true">${content.header().description()}</textarea>
    <#-- A compléter avec les champs présents dans la spec ... -->
</#macro>