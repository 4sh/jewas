<#macro createContentHeader>

    <div id="author"></div>

    <div class="create_line">
        <div class="create_label"><label for="title" class="style_label">Titre</label> :</div>
        <div class="create_field"><input type="text" id="title" name="title" maxlength="64" required="required" style="width:557px;"/></div>
    </div>

    <div>
        <div><label for="description" class="style_label">Description</label> :</div>
        <div class="create_textarea"><textarea class="textarea_description" id="description" name="description" maxlength="500" required="required"></textarea></div>
    </div>

    <div class="create_line">
        <div class="create_label"><label for="domains" class="style_label">Domaines</label> :</div>
        <div class="create_field"><select id="domains" name="domains" class="chzn-select side-by-side clearfix" multiple style="width:567px;"></select></div>
    </div>

    <div class="create_line">
        <div class="create_label"><label for="tags" class="style_label">Mots clés</label> : </div>
        <div class="create_field"><select id="tags" name="tags" class="chzn-select side-by-side clearfix chzn-extendable" multiple style="width:567px;"></select></div>
    </div>

</#macro>