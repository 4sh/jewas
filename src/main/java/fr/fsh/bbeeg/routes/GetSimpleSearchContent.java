package fr.fsh.bbeeg.routes;

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

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<ContentSearchResult> results = new ArrayList<ContentSearchResult>();

                results.add(new ContentSearchResult().id("1").author("fcamblor").title("Premier contenu").mediaType("audio"));
                results.add(new ContentSearchResult().id("2").author("fcamblor").title("Second contenu").mediaType("video"));

                request.respondJson().object(results);
            }
        };
    }
}
