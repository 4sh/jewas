function ChainedSelect(_configuration){

    var configuration = _configuration;

    (function(){
    }).call(this);

    this.decorateSelectFieldWithChainedSelectConfiguration = function(selectField){
        selectField.change(function(){
           $(this).find("option:selected").each(function(){
               addSelectedElement($(this));
           });
        });
    };

    function addSelectedElement(selectedOption){
        // Updating targetFieldValue
        var targetFieldValue = configuration.targetFieldForSelectedOption().val();
        var currentDepth = 0;
        if(targetFieldValue != ""){
            targetFieldValue += "||";
            currentDepth = targetFieldValue.split("||").length-1;
        }
        targetFieldValue += selectedOption.val();
        configuration.targetFieldForSelectedOption().val(targetFieldValue);

        // Updating displaySelectionTarget
        var itemHtml = configuration.templateForDisplaySelectionItem().tmpl({ label: selectedOption.text() });
        $(itemHtml).appendTo(configuration.displaySelectionTarget());

        // Updating select content
        var selectElement = selectedOption.parents('select').first();
        selectElement.empty(); // Resetting select content
        // Updating select content only if we aren't on the last depth
        var ajaxUrlToCall = configuration.resolveAjaxUrlForOptionSelectedHandler().call(configuration, currentDepth+1, selectedOption);
        if(ajaxUrlToCall != null){
            $.get(ajaxUrlToCall, function(data) {
                 $(data).each(function(){
                     var resolvedOption = configuration.rowMappingHandler().call(null, currentDepth+1, this);
                     selectElement.append($('<option></option>').val(resolvedOption.value).html(resolvedOption.label));
                 });

                // (for chosen integration) Saying chosen "hey you should update the select menu"
                selectElement.trigger("liszt:updated");
            }, "json");
        } else {
            // (for chosen integration) Refreshing chosen select menu to empty it
            selectElement.trigger("liszt:updated");
            // If we are on the last depth, let's hide the select menu
            if(configuration.selectMenuContainer() != null){
                configuration.selectMenuContainer().hide();
            }
        }
    };
};
ChainedSelect.Configuration = function(){
    function ConfigurationConstructor() {
        // ajaxUrlsPerDepth is a map [depth element starting by 0 => url] which will be used,
        // by default, il the resolveAjaxUrlForOptionSelectedHandler.
        // You can provide in your url special strings "{value}" and "{label}" if you want to pass
        // parameters to your ajax url call
        var _ajaxUrlsPerDepth;          // MANDATORY
        this.ajaxUrlsPerDepth = function(__ajaxUrlsPerDepth){ if(__ajaxUrlsPerDepth==null){ return _ajaxUrlsPerDepth; } else { _ajaxUrlsPerDepth = __ajaxUrlsPerDepth; return this; } };
        // Default implementation will use the ajaxUrlsPerDepth map
        // to retrieve url for depth <depth> if <selectedOption> has been selected
        // The méthod should return null if we are on the last depth
        var _resolveAjaxUrlForOptionSelectedHandler = function(depth, selectedOption){
            if(depth < this.ajaxUrlsPerDepth().length){
                return this.ajaxUrlsPerDepth()[depth]
                    .replace(ChainedSelect.Configuration.VALUE_REGEX, selectedOption.val())
                    .replace(ChainedSelect.Configuration.LABEL_REGEX, selectedOption.text());
            } else {
                return null;
            }
        };
        this.resolveAjaxUrlForOptionSelectedHandler = function(__resolveAjaxUrlForOptionSelectedHandler){ if(__resolveAjaxUrlForOptionSelectedHandler==null){ return _resolveAjaxUrlForOptionSelectedHandler; } else { _resolveAjaxUrlForOptionSelectedHandler = __resolveAjaxUrlForOptionSelectedHandler; return this; } };
        // Default implementation will consider the jsonObjects returned will have
        // a value and label fields to map into the lines, no matter the depth is
        var _rowMappingHandler = function(depth, jsonObject){
            return { value: jsonObject.value, label: jsonObject.label };
        };
        this.rowMappingHandler = function(__rowMappingHandler){ if(__rowMappingHandler==null){ return _rowMappingHandler; } else { _rowMappingHandler = __rowMappingHandler; return this; } };
        // Field (hidden generaly) that will welcome the hierarchical path representing selected values
        var _targetFieldForSelectedOption;  // MANDATORY
        this.targetFieldForSelectedOption = function(__targetFieldForSelectedOption){ if(__targetFieldForSelectedOption==null){ return _targetFieldForSelectedOption; } else { _targetFieldForSelectedOption = __targetFieldForSelectedOption; return this; } };
        // Template that will be used to display a selected item
        var _templateForDisplaySelectionItem;
        this.templateForDisplaySelectionItem = function(__templateForDisplaySelectionItem){ if(__templateForDisplaySelectionItem==null){ return _templateForDisplaySelectionItem; } else { _templateForDisplaySelectionItem = __templateForDisplaySelectionItem; return this; } };
        // HTML Element where the selected hierarchy will be displayed
        var _displaySelectionTarget;     // MANDATORY
        this.displaySelectionTarget = function(__displaySelectionTarget){ if(__displaySelectionTarget==null){ return _displaySelectionTarget; } else { _displaySelectionTarget = __displaySelectionTarget; return this; } };
        var _selectMenuContainer;
        this.selectMenuContainer = function(__selectMenuContainer){ if(__selectMenuContainer==null){ return _selectMenuContainer; } else { _selectMenuContainer = __selectMenuContainer; return this; } };
    }
    return ConfigurationConstructor;
}();
ChainedSelect.Configuration.VALUE_REGEX = new RegExp("\\{value\\}", "ig");
ChainedSelect.Configuration.LABEL_REGEX = new RegExp("\\{label\\}", "ig");
