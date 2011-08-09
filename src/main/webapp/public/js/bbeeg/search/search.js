/**
 * This class needs following scripts to work properly :
 * - jquery
 */

function SearchQuery() {

    var lastQueryParameters = null;

    (function() {
    }).call(this);

    function QuerySnapshot() {
        var _parameters;
        this.parameters = function(__parameters) {
            if (__parameters == null) {
                return _parameters;
            } else {
                _parameters = __parameters;
                return this;
            }
        };
        var _searchContext;
        this.searchContext = function(__searchContext) {
            if (__searchContext == null) {
                return _searchContext;
            } else {
                _searchContext = __searchContext;
                return this;
            }
        };
        var _endingOffset;
        this.endingOffset = function(__endingOffset) {
            if (__endingOffset == null) {
                return _endingOffset;
            } else {
                _endingOffset = __endingOffset;
                return this;
            }
        };
        var _serverTimestamp;
        this.serverTimestamp = function(__serverTimestamp) {
            if (__serverTimestamp == null) {
                return _serverTimestamp;
            } else {
                _serverTimestamp = __serverTimestamp;
                return this;
            }
        };
        this.generateNextQueryParameters = function() {
            return this.parameters() + "&startingOffset=" + (this.endingOffset() + 1) + "&serverTimestamp=" + this.serverTimestamp();
        }
    }

    this.search = function(searchContext) {
        contentSearch(searchContext, searchContext.targetForm().serialize(), searchContext.globalResultTemplate(), false);
    }

    this.searchNext = function() {
        // In append mode, retrieving searchContext from lastQueryParameters
        var searchContext = lastQueryParameters.searchContext();
        contentSearch(searchContext, lastQueryParameters.generateNextQueryParameters(), searchContext.resultElementTemplate(), true);
    }

    /**
     * Will perform a search and return a ContentSearchResult[]
     * @param searchContext Information on searchContext
     * @param queryParams String passed to the form's resourceUrl as parameters
     * @param templateApplied Template which will be used to render things returned by resourceUrl
     * @param appendResults Says if we should be in append mode or not
     * (that is to say : will we reset the results or append results to the end of displayed results ?)
     *
     */
    function contentSearch(searchContext, queryParams, templateApplied, appendResults) {
        $.get(searchContext.targetForm().attr('action'), queryParams, function(data) {
            $(searchContext.selectorForClickableOfSearchNext()).each(function() {
                $(".spinner", this).css('display', 'none');
                this.disabled = false;
            });
            
            // If we are not in append mode, let's remember the last query and the server timestamp of this query
            // It will be used when searching in append mode ! (look above)
            if (!appendResults) {
                lastQueryParameters = new QuerySnapshot().parameters(queryParams)
                    .serverTimestamp(data.serverTimestamp)
                    .searchContext(searchContext);
            }

            // Updating last offset of lastQueryParameters
            lastQueryParameters.endingOffset(data.endingOffset);

            var templateParams = data;

            // In append mode, we want to directly pass to the template the search results
            // since we want to append only new results
            if (appendResults) {
                templateParams = data.results;
            }

            // Inserting things via a template
            var html = templateApplied.tmpl(templateParams);
            if (appendResults) {
                $(searchContext.selectorWhereResultsWillBeAppended()).each(function() {
                    html.appendTo($(this));
                });
            } else {
                searchContext.resultElement().html(html);
            }

            // Binding clickables for "next search"
            if (!appendResults) {
                $(searchContext.selectorForClickableOfSearchNext()).each(function() {
                    var clickable = this;
                    $(this).click(function() {
                        this.disabled = true;
                        $(".spinner", this).css('display', 'inline');
                        SearchQuery.INSTANCE.searchNext();
                    });
                });
            }
        }, "json");
    }
}
// Used for forms having a submit button inside it
SearchQuery.bindSearchToForm = function(searchContext) {
    searchContext.targetForm().submit(function() {
        SearchQuery.INSTANCE.search(searchContext);
        return false; // Ensuring form is not really submitted
    });
};
// Used for non-submit buttons
SearchQuery.bindSearchToClickable = function(clickableElement, searchContext) {
    clickableElement.click(function() {
        // Wondering if we shouldn't explicitely put searchContext.appendResult(true) here ...
        SearchQuery.INSTANCE.search(searchContext);
    });
};
SearchQuery.bindSearchNextToClickable = function(clickableElement) {
    clickableElement.click(function() {
        SearchQuery.INSTANCE.searchNext();
    });
}
// Inner class SearchContext
SearchQuery.SearchContext = function() {
    function SearchContextConstructor() {
        /**
         * HTML Element where results will be inserted
         */
        var _resultElement;
        this.resultElement = function(__resultElement) {
            if (__resultElement == null) {
                return _resultElement;
            } else {
                _resultElement = __resultElement;
                return this;
            }
        };

        /**
         * Form that will be submitted during the search.
         * It must contain, as its "action" attribute, the url accessed in ajax
         */
        var _targetForm;
        this.targetForm = function(__targetForm) {
            if (__targetForm == null) {
                return _targetForm;
            } else {
                _targetForm = __targetForm;
                return this;
            }
        };


        /**
         * JQuery template element (<script> tag) used to render every search results (things in resultElement)
         */
        var _globalResultTemplate;
        this.globalResultTemplate = function(__globalResultTemplate) {
            if (__globalResultTemplate == null) {
                return _globalResultTemplate;
            } else {
                _globalResultTemplate = __globalResultTemplate;
                return this;
            }
        };

        /**
         * JQuery template element (<script> tag) used to render only one occurence of a result
         */
        var _resultElementTemplate;
        this.resultElementTemplate = function(__resultElementTemplate) {
            if (__resultElementTemplate == null) {
                return _resultElementTemplate;
            } else {
                _resultElementTemplate = __resultElementTemplate;
                return this;
            }
        };

        /**
         * CSS selector where we will append results during a searchNext()
         */
        var _selectorWhereResultsWillBeAppended;
        this.selectorWhereResultsWillBeAppended = function(__selectorWhereResultsWillBeAppended) {
            if (__selectorWhereResultsWillBeAppended == null) {
                return _selectorWhereResultsWillBeAppended;
            } else {
                _selectorWhereResultsWillBeAppended = __selectorWhereResultsWillBeAppended;
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

        /**
         * Html selector for the clickables where we will attach the "searchNext" call
         */
        var _selectorForClickableOfSearchNext;
        this.selectorForClickableOfSearchNext = function(__selectorForClickableOfSearchNext) {
            if (__selectorForClickableOfSearchNext == null) {
                return _selectorForClickableOfSearchNext;
            } else {
                _selectorForClickableOfSearchNext = __selectorForClickableOfSearchNext;
                return this;
            }
        };
    }

    return SearchContextConstructor;
}();
// SearchQuery singleton instance
// Can't do this privately (and overall, "uniquely") in JS ... too bad :(
// Wondering if we should keep the singleton model for this since the SearchQuery instance
// is not stateless anymore (with the lastQueryParameters attribute)
// Maybe, we could have several query search form / results per page ... and when we will be in this
// case, using a singleton instance will hurt.
SearchQuery.INSTANCE = new SearchQuery();

