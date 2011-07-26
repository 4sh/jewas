package fr.fsh.bbeeg.routes;

import jewas.http.*;
import jewas.template.Templates;

/**
 * @author fcamblor
 */
public class GetSearchRoute extends AbstractRoute {

    public GetSearchRoute() {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/search.html"));
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondHtml().content(Templates.process("search/search.ftl", null));
            }
        };
    }
}
