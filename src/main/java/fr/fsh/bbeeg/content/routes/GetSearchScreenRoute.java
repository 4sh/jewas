package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.pojos.SearchMode;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.template.Templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author driccio
 */
public class GetSearchScreenRoute extends AbstractRoute {

    public GetSearchScreenRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/search.html"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("searchMode", SearchMode.ALL_VALIDATED.ordinal());

                request.respondHtml().content(Templates.process("content/search.ftl", params));
            }
        };
    }
}