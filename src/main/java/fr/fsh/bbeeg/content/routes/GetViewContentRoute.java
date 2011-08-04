package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
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
public class GetViewContentRoute extends AbstractRoute {
    public GetViewContentRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/[id]/view.html"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", oi.id());
                request.respondHtml().content(Templates.process("content/view.ftl", params));
            }
        };
    }
}