package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.resources.ContentSearchResult;
import jewas.http.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fcamblor
 */
public class GetSimpleSearchContent extends AbstractRoute {
    public GetSimpleSearchContent() {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/search"));
    }

    // FIXME : to externalize into resources package (when ContentQueryObject will have been renamed)
    public static class SearchQueryObject {
        private String query;

        public SearchQueryObject query(String _query) {
            this.query = _query;
            return this;
        }

        public String query() {
            return this.query;
        }
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final SearchQueryObject query = toQueryObject(parameters, SearchQueryObject.class);
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<ContentSearchResult> results = new ArrayList<ContentSearchResult>();

                results.add(new ContentSearchResult().id("1").author("fcamblor").title("Premier contenu").mediaType("audio"));
                results.add(new ContentSearchResult().id("2").author("fcamblor").title("Second contenu").mediaType("video"));

                if (query.query().contains("easterEgg")) {
                    results.add(new ContentSearchResult().id("3").author("fcamblor").title("Troisieme contenu (caché)").mediaType("eeg"));
                }

                request.respondJson().object(results);
            }
        };
    }
}
