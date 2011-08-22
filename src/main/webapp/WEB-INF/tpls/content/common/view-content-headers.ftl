<#macro viewContentHeaders content>
    <p>Auteur : ${content.header().author().name()}</p>
    <p>Intitulé : ${content.header().title()}</p>
    <p>Description : </p> <textarea rows="3" cols="100" readonly="true">${content.header().description()}</textarea>
    <#-- A compléter avec les champs présents dans la spec ... -->
</#macro>