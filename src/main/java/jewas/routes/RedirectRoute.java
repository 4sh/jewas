package jewas.routes;

import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author fcamblor
 */
public class RedirectRoute extends AbstractRoute {

    private String redirectLocation;

    public RedirectRoute(String uri, String redirectLocation){
        super(HttpMethodMatcher.ALL, new PatternUriPathMatcher(uri));
        this.redirectLocation = redirectLocation;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.redirect().location(redirectLocation);
            }
        };
    }
}
