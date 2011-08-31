package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.pojos.SearchMode;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author driccio
 */
public class GetUserContentsSearchScreenRoute extends AbstractRoute {

    public GetUserContentsSearchScreenRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/search-user-content.html"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("searchMode", SearchMode.ONLY_USER_CONTENTS.ordinal());

                request.respondHtml().content(Templates.process("content/search.ftl", params));
            }
        };
    }
}