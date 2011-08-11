package fr.fsh.bbeeg.content.routes;

import jewas.http.*;

/**
 * @author fcamblor
 */
public class CreateContentRoute extends AbstractRoute {

    public CreateContentRoute(){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/content"));
    }
    
    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // TODO: to complete !
                request.respondJson().object("Ok");
            }
        };
    }
}
