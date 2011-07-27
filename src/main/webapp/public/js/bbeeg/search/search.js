function SearchQuery() {

    (function() {
    }).call(this);

    this.simpleContentSearch = function(uniqueSearchField, resultElement) {
        return search("/content/search", { q: uniqueSearchField.val() }, resultElement);
    };

    this.advancedContentSearch = function(resultElement/* TODO */) {
        alert('Not yet implemented :-)');
    };

    /**
     * Will perform a search and return a ContentSearchResult[]
     * @param resourceUrl REST URL returning an array of search results
     * @param parameters Query parameters given to the resourceUrl
     */
    function search(resourceUrl, parameters, resultElement) {
        $.get(resourceUrl, parameters, function(data) {
            displayInto(resultElement, data);
        }, "json");
    }

    ;

    function displayInto(resultElement, results) {
        // TODO: refactor this...
        $('<ul>').appendTo(resultElement);
        $.each(results, function(index, res) {
            $('<li>' + res.title + '</li>').appendTo(resultElement);
        });
        $('</ul>').appendTo(resultElement);
    }

    ;
}
SearchQuery.registerClickableForSimpleSearch = function(clickableElement, uniqueSearchField, resultElement) {
    clickableElement.click(function() {
        SearchQuery.INSTANCE.simpleContentSearch(uniqueSearchField, resultElement);
    });
};
SearchQuery.registerClickableForAdvancedSearch = function(clickableElement /*, TODO */, resultElement) {
    clickableElement.click(function() {
        SearchQuery.INSTANCE.advancedContentSearch(resultElement);
    });
};
// Can't do this privately (and overall, "uniquely") in JS ... too bad :(
SearchQuery.INSTANCE = new SearchQuery();

