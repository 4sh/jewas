<#include "../common/mainTemplate.ftl">

<@mainTemplate title="Création d'un contenu" selectedMenuItem="" scripts=["/public/js/jewas/jewas-forms.js"] >
<script type="text/javascript">
    $(function() {
        $("#confirmationDialog").dialog({
            autoOpen: false,
            modal: false,
            show: 'drop',
            hide: 'drop'
        });
        $("#createContent").submit(function(){
            form = this;
            $.put(this.action, $(this).serialize(), function(data){
               $("#confirmationDialog").dialog('open');
                setTimeout(function(){
                    $("#confirmationDialog").dialog('close');
                }, 2000);
               form.reset();
           });
           return false;
        });
    });
</script>

    <div id="confirmationDialog" title="Succès">
        <p>Votre contenu a été créé avec succès !</p>
    </div>

    <h3>Création d'un contenu</h3>

    <form id="createContent" action="/content" method="post">
        <p>Auteur : toto</p>
        <p><label for="title">Titre</label> : <input type="text" id="title" name="title" /></p>
        <p><label for="content">Contenu</label> :<br/><textarea rows="8" cols="100" id="content" name="content"></textarea></p>
        <p><input type="submit" value="Enregistrer" /></p>
    </form>

</@mainTemplate>