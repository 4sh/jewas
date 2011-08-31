package fr.fsh.bbeeg.user.routes;

import fr.fsh.bbeeg.user.resources.UserResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class GetUserInformationsRoute extends AbstractRoute {
    public GetUserInformationsRoute() {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/user/infos"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(UserResource.getUserInformations());
            }
        };
    }
}