package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.ContentResource;
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
    private ContentResource contentResource;

    public GetViewContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/[id]/view.html"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("content", contentResource.getContentById(oi.id()));
                request.respondHtml().content(Templates.process("content/view.ftl", params));
            }
        };
    }
}
