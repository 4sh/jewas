package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetTotalNumberOfContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetTotalNumberOfContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/count"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(contentResource.getContentCount());
            }
        };
    }
}
