package fr.fsh.bbeeg.user.routes;

import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
import fr.fsh.bbeeg.user.resources.UserResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class GetUserInformationsRoute extends AbstractRoute {

    private final UserResource userResource;

    public GetUserInformationsRoute(UserResource _userResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/user/infos"));
        this.userResource = _userResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                String securityToken = HttpRequestHelper.getSecurityToken(request);
                request.respondJson().object(userResource.getUserInformations(securityToken));
            }
        };
    }
}