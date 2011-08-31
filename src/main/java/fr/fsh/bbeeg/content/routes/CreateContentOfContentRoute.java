package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpHeaders;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class CreateContentOfContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public CreateContentOfContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/content/content/[id]"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);
        String contentType = request.headers().getHeaderValue(HttpHeaders.CONTENT_TYPE);

        if (contentType == null) {
            // TODO: do something
        }

//        ByteBuffer content;
//
//        if ("text".equals(contentType)) {
//            // Create a file...
//
//        } else {
//            // Should be a file
//            request.
//        }

        contentResource.updateContentOfContent(oi.id(), contentType, request.content());

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // TODO: to complete !

                request.respondJson().object("Ok");
            }
        };
    }
}
