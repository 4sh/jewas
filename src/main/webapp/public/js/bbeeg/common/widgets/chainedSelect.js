(function( $ ){
  $.fn.chainedSelect = function(options) {

      VALUE_REGEX = new RegExp("\\{value\\}", "ig");
      LABEL_REGEX = new RegExp("\\{label\\}", "ig");
      HIERARCHY_SEPARATOR = "/";

      var settings = {
        // ajaxUrlsPerDepth is a map [depth element starting by 0 => url] which will be used,
        // by default, il the resolveAjaxUrlForOptionSelectedHandler.
        // You can provide in your url special strings "{value}" and "{label}" if you want to pass
        // parameters to your ajax url call
        'ajaxUrlsPerDepth' : null, // MANDATORY
        // Default implementation will use the ajaxUrlsPerDepth map
        // to retrieve url for depth <depth> if <selectedOption> has been selected
        // The méthod should return null if we are on the last depth
        'resolveAjaxUrlForOptionSelectedHandler' : function(depth, selectedOption){
            if(depth < this.ajaxUrlsPerDepth.length){
                if(selectedOption == null){
                    return this.ajaxUrlsPerDepth[depth];
                } else {
                    return this.ajaxUrlsPerDepth[depth]
                        .replace(VALUE_REGEX, selectedOption.val())
                        .replace(LABEL_REGEX, selectedOption.text());
                }
            } else {
                return null;
            }
        },
        // Default implementation will consider the jsonObjects returned will have
        // a value and label fields to map into the lines, no matter the depth is
        'rowMappingHandler' : function(depth, jsonObject){
            return { value: jsonObject.value, label: jsonObject.label };
        },
        // Field (hidden generaly) that will welcome the hierarchical path representing selected values
        'targetFieldForSelectedOption' : null,  // MANDATORY
        // Template that will be used to display a selected item
        // IMPORTANT Notes:
        // - The template MUST start with a <li> tag
        // - It must include a <a> tag with name="{{= value}}"
        'templateForDisplaySelectionItem' : null,  // MANDATORY
        // JQuery selector to retrieve closing links in _templateForDisplaySelectionItem
        'selectorForClosingLinkInDisplaySelectionItemTemplate' : null, // MANDATORY
        // HTML Element where the selected hierarchy will be displayed
        'displaySelectionTarget' : null,     // MANDATORY
        'selectMenuContainer' : null
      };

      return this.each(function () {
          var $this = $(this)

          var opts = $.extend({}, settings, options);
          $this.change(function(){
               $(this).find("option:selected").each(function(){
                   addSelectedElement($(this), opts);
               });
          });

          // Initializing select menu with first ajax url contents
          updateSelectContentDependingOn(-1, null, opts);
      });
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
        var values = path.split(HIERARCHY_SEPARATOR);
        for(var i=0; i<values.length && values[i] != directoryName; i++){
            if(i != 0){
                result += HIERARCHY_SEPARATOR;
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
        var values = path.split(HIERARCHY_SEPARATOR);
        if(values.length == 1){
            return "";
        } else {
            return path.substring(0,
                path.length - HIERARCHY_SEPARATOR.length - values[values.length-1].length);
        }
    }

    function addSelectedElement(selectedOption, opts){
        // Updating targetFieldValue
        var targetFieldValue = opts.targetFieldForSelectedOption.val();
        var currentDepth = 0;
        if(targetFieldValue != ""){
            targetFieldValue += HIERARCHY_SEPARATOR;
            currentDepth = targetFieldValue.split(HIERARCHY_SEPARATOR).length-1;
        } else {
            // When adding the first element, we should show the displaySelectionTarget div
            opts.displaySelectionTarget.show();
        }
        targetFieldValue += selectedOption.val();
        opts.targetFieldForSelectedOption.val(targetFieldValue);

        // Updating displaySelectionTarget
        var itemHtml = opts.templateForDisplaySelectionItem.tmpl({ value: selectedOption.val(), label: selectedOption.text() });
        $(itemHtml).appendTo(opts.displaySelectionTarget);
        // Updating click event on closing links
        $(opts.selectorForClosingLinkInDisplaySelectionItemTemplate, opts.displaySelectionTarget).each(function(){
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
                var newTargetFieldValue = removeDirFromPath(valueToDelete, opts.targetFieldForSelectedOption.val());
                // Deleting last value since we will "re-add" it just after via addSelectedElement()
                newTargetFieldValue = removeLastDir(newTargetFieldValue);
                opts.targetFieldForSelectedOption.val(newTargetFieldValue);

                var currentLiTag = hyperlink.parents('li').first();

                // Deleting every <li> tag after current <li> tag
                $("~li", currentLiTag).remove();

                var previousLi = currentLiTag.prev();
                // Deleting current <li> tag
                currentLiTag.remove();

                // Displaying select menu
                opts.selectMenuContainer.show();

                // Deleting previous <li> tag since we will "re-add" it just after via a addSelectedElement()
                if(previousLi.length != 0){
                    // Ugly code here with hardcoded things :(
                    var previousLabel = $("span", previousLi).text();
                    var previousValue = $("a", previousLi).attr('name');

                    previousLi.remove();

                    // Building & re-adding the previously deleted option
                    var selectElement = $("select", opts.selectMenuContainer).first();
                    selectElement.empty();
                    var fakeSelectedElement = $('<option></option>').val(previousValue).html(previousLabel);
                    selectElement.append(fakeSelectedElement);
                    fakeSelectedElement = $("option", selectElement).first();
                    addSelectedElement(fakeSelectedElement, opts);
                } else {
                    updateSelectContentDependingOn(-1, null, opts);
                }
            });
        });

        updateSelectContentDependingOn(currentDepth, selectedOption, opts);
    }

    function updateSelectContentDependingOn(currentDepth, selectedOption, opts){
        if(currentDepth == -1){
            // Ensuring displaySelectionTarget div is hidden since when empty,
            // we should display it (it there are border on the div, it will be ugly if
            // displayed when empty)
            opts.displaySelectionTarget.hide();
        }

        // Updating select content
        var selectElement = $("select", opts.selectMenuContainer).first();
        selectElement.empty(); // Resetting select content
        // Updating select content only if we aren't on the last depth
        var ajaxUrlToCall = opts.resolveAjaxUrlForOptionSelectedHandler.call(opts, currentDepth+1, selectedOption);
        if(ajaxUrlToCall != null){
            $.get(ajaxUrlToCall, function(data) {
                 $(data).each(function(){
                     var resolvedOption = opts.rowMappingHandler.call(null, currentDepth+1, this);
                     selectElement.append($('<option></option>').val(resolvedOption.value).html(resolvedOption.label));
                 });

                // (for chosen integration) Saying chosen "hey you should update the select menu"
                selectElement.trigger("liszt:updated");
            }, "json");
        } else {
            // (for chosen integration) Refreshing chosen select menu to empty it
            selectElement.trigger("liszt:updated");
            // If we are on the last depth, let's hide the select menu
            if(opts.selectMenuContainer != null){
                opts.selectMenuContainer.hide();
            }
        }
    };
})( jQuery );

