package fr.fsh.bbeeg.user.routes;

import fr.fsh.bbeeg.user.resources.UserResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.json.Json;

/**
 * @author driccio
 */
public class GetUserInformationsRoute extends AbstractRoute {
     public GetUserInformationsRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/user/infos"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(Json.instance().toJsonString(UserResource.getUserInformations()));
            }
        };
    }
}
