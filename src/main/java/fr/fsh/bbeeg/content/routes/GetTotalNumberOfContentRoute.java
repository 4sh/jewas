package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.json.Json;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetTotalNumberOfContentRoute extends AbstractRoute {

    public GetTotalNumberOfContentRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/count"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(Json.instance().toJsonString(ContentResource.getContentCount()));
            }
        };
    }
}
