<#macro viewContentHeaders content>
    <p>Auteur : ${content.author().name()}</p>
    <p>Intitulé : ${content.title()}</p>
    <#-- A compléter avec les champs présents dans la spec ... -->
</#macro>