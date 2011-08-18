package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;

/**
 * @author fcamblor
 */
public class CreateContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public CreateContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/content"));
        contentResource = _contentResource;
    }
    
    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // TODO: to complete !
               //createContentResource.createTextContent();

                request.respondJson().object("Ok");
            }
        };
    }
}
