package fr.fsh.bbeeg.security.routes;

import fr.fsh.bbeeg.security.resources.ConnectedUserResource;
import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * User: carmarolli
 */
public class GetConnectedUserRoute extends AbstractRoute {
 private ConnectedUserResource connectedUserResource;

    public GetConnectedUserRoute(ConnectedUserResource _connectedUserResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/connectedUser"));
        this.connectedUserResource = _connectedUserResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                String securityToken = HttpRequestHelper.getSecurityToken(request);
                request.respondJson().object(connectedUserResource.getUserSecurity(securityToken));
            }
        };
    }
}
