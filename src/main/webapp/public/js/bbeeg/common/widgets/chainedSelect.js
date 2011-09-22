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

        // Initializing select menu with first ajax url contents
        updateSelectContentDependingOn(-1, null);
    };

    /**
     * Will remove directoryName from path. It will remove directoryName's children
     * in path, too.
     * ie removeDirFromPath("bar", "foo/bar/baz") will return "foo"
     * @param valueToDelete
     * @param fullTargetFieldValue
     */
    function removeDirFromPath(directoryName, path){
        var result = "";

        // Looking for directoryName
        var values = path.split(ChainedSelect.HIERARCHY_SEPARATOR);
        for(var i=0; i<values.length && values[i] != directoryName; i++){
            if(i != 0){
                result += ChainedSelect.HIERARCHY_SEPARATOR;
            }
            result += values[i];
        }

        return result;
    }

    /**
     * Will remove last path in given path and return the resulting path
     * ie removeLastDir("foo/bar/baz") will return "foo/bar"
     * @param fullTargetFieldValue
     */
    function removeLastDir(path){
        var values = path.split(ChainedSelect.HIERARCHY_SEPARATOR);
        if(values.length == 1){
            return "";
        } else {
            return path.substring(0,
                path.length - ChainedSelect.HIERARCHY_SEPARATOR.length - values[values.length-1].length);
        }
    }

    function addSelectedElement(selectedOption){
        // Updating targetFieldValue
        var targetFieldValue = configuration.targetFieldForSelectedOption().val();
        var currentDepth = 0;
        if(targetFieldValue != ""){
            targetFieldValue += ChainedSelect.HIERARCHY_SEPARATOR;
            currentDepth = targetFieldValue.split(ChainedSelect.HIERARCHY_SEPARATOR).length-1;
        }
        targetFieldValue += selectedOption.val();
        configuration.targetFieldForSelectedOption().val(targetFieldValue);

        // Updating displaySelectionTarget
        var itemHtml = configuration.templateForDisplaySelectionItem().tmpl({ value: selectedOption.val(), label: selectedOption.text() });
        $(itemHtml).appendTo(configuration.displaySelectionTarget());
        // Updating click event on closing links
        $(configuration.selectorForClosingLinkInDisplaySelectionItemTemplate(), configuration.displaySelectionTarget()).each(function(){
            // Unbinding previously affected click event
            $(this).unbind("click");

            // Re-binding click event
            var hyperlink = $(this);
            $(this).click(function(){
                // For the moment, we will hardcode that we need templateForDisplaySelectionItem() to begin with
                // a <li> tag ..

                // Updating targetFieldValue
                // It's not perfect to put select value in hyperlink's name but heh .. doesn't have a better idea for
                // the moment to keep things "templatable"
                var valueToDelete = hyperlink.attr('name');
                var newTargetFieldValue = removeDirFromPath(valueToDelete, configuration.targetFieldForSelectedOption().val());
                // Deleting last value since we will "re-add" it just after via addSelectedElement()
                newTargetFieldValue = removeLastDir(newTargetFieldValue);
                configuration.targetFieldForSelectedOption().val(newTargetFieldValue);

                var currentLiTag = hyperlink.parents('li').first();

                // Deleting every <li> tag after current <li> tag
                $("~li", currentLiTag).remove();

                var previousLi = currentLiTag.prev();
                // Deleting current <li> tag
                currentLiTag.remove();

                // Displaying select menu
                configuration.selectMenuContainer().show();

                // Deleting previous <li> tag since we will "re-add" it just after via a addSelectedElement()
                if(previousLi.length != 0){
                    // Ugly code here with hardcoded things :(
                    var previousLabel = $("span", previousLi).text();
                    var previousValue = $("a", previousLi).attr('name');

                    previousLi.remove();

                    // Building & re-adding the previously deleted option
                    var selectElement = $("select", configuration.selectMenuContainer()).first();
                    selectElement.empty();
                    var fakeSelectedElement = $('<option></option>').val(previousValue).html(previousLabel);
                    selectElement.append(fakeSelectedElement);
                    fakeSelectedElement = $("option", selectElement).first();
                    addSelectedElement(fakeSelectedElement);
                } else {
                    updateSelectContentDependingOn(-1, null);
                }
            });
        });

        updateSelectContentDependingOn(currentDepth, selectedOption);
    }

    function updateSelectContentDependingOn(currentDepth, selectedOption){
        // Updating select content
        var selectElement = $("select", configuration.selectMenuContainer()).first();
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
                if(selectedOption == null){
                    return this.ajaxUrlsPerDepth()[depth];
                } else {
                    return this.ajaxUrlsPerDepth()[depth]
                        .replace(ChainedSelect.Configuration.VALUE_REGEX, selectedOption.val())
                        .replace(ChainedSelect.Configuration.LABEL_REGEX, selectedOption.text());
                }
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
        // IMPORTANT Notes:
        // - The template MUST start with a <li> tag
        // - It must include a <a> tag with name="{{= value}}"
        var _templateForDisplaySelectionItem;  // MANDATORY
        this.templateForDisplaySelectionItem = function(__templateForDisplaySelectionItem){ if(__templateForDisplaySelectionItem==null){ return _templateForDisplaySelectionItem; } else { _templateForDisplaySelectionItem = __templateForDisplaySelectionItem; return this; } };
        // JQuery selector to retrieve closing links in _templateForDisplaySelectionItem
        var _selectorForClosingLinkInDisplaySelectionItemTemplate; // MANDATORY
        this.selectorForClosingLinkInDisplaySelectionItemTemplate = function(__selectorForClosingLinkInDisplaySelectionItemTemplate){ if(__selectorForClosingLinkInDisplaySelectionItemTemplate==null){ return _selectorForClosingLinkInDisplaySelectionItemTemplate; } else { _selectorForClosingLinkInDisplaySelectionItemTemplate = __selectorForClosingLinkInDisplaySelectionItemTemplate; return this; } };
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
ChainedSelect.HIERARCHY_SEPARATOR = "/";
