/**
 * This class needs following scripts to work properly :
 * - jquery
 */

function SearchQuery() {

    (function() {
    }).call(this);

    /**
     * Will perform a search and return a ContentSearchResult[]
     * @param searchContext Information on searchContext
     */
    this.contentSearch = function(searchContext) {
        $.get(searchContext.targetForm().attr('action'), searchContext.targetForm().serialize(), function(data) {
            // Boxing results array into a container, in order to not iterate on the root template
            var templateParams = { results: data };
            // If we are in append mode, resultTemplate should be directly the template where we will iterate over
            /*
             if(searchContext.appendResults()){
             templateParams = data;
             }*/
            useTemplateInto(searchContext.resultTemplate(), templateParams, searchContext.resultElement()/*, searchContext.appendResults()*/);
        }, "json");
    }

        ;

    function useTemplateInto(template, parameters, elementWhereTemplateIsApplied/*, append*/) {
        var html = template.tmpl(parameters);
        /*if(append){
         html.appendTo(elementWhereTemplateIsApplied);
         } else {*/
        elementWhereTemplateIsApplied.html(html);
        //}
    }

    ;
}
SearchQuery.registerSearch = function(searchContext) {
    searchContext.targetForm().submit(function() {
        SearchQuery.INSTANCE.contentSearch(searchContext);
        return false; // Ensuring form is not really submitted
    });
};
/*
 SearchQuery.registerClickableForContentSearch = function(clickableElement, searchContext) {
 searchContext.targetForm().click(function() {
 SearchQuery.INSTANCE.contentSearch(searchContext);
 });
 };
 */
// Inner class SearchContext
SearchQuery.SearchContext = function() {
    function SearchContextConstructor() {
        var _resultElement;
        this.resultElement = function(__resultElement) {
            if (__resultElement == null) {
                return _resultElement;
            } else {
                _resultElement = __resultElement;
                return this;
            }
        };
        var _targetForm;
        this.targetForm = function(__targetForm) {
            if (__targetForm == null) {
                return _targetForm;
            } else {
                _targetForm = __targetForm;
                return this;
            }
        };
        var _resultTemplate;
        this.resultTemplate = function(__resultTemplate) {
            if (__resultTemplate == null) {
                return _resultTemplate;
            } else {
                _resultTemplate = __resultTemplate;
                return this;
            }
        };
        /*
         var _appendResults;
         this.appendResults = function(__appendResults){ if(__appendResults==null){ return _appendResults; } else { _appendResults = __appendResults; return this; } };
         */
    }

    return SearchContextConstructor;
}();
// SearchQuery singleton instance
// Can't do this privately (and overall, "uniquely") in JS ... too bad :(
SearchQuery.INSTANCE = new SearchQuery();

