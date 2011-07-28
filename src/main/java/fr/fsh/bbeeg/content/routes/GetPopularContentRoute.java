package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
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
public class GetPopularContentRoute extends AbstractRoute {

    public GetPopularContentRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/popular/[number]"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(Json.instance().toJsonString(ContentResource.getPopularContent(qo)));
            }
        };
    }
}
