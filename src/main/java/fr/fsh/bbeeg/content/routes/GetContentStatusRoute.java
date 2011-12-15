package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author carmarolli
 */
public class GetContentStatusRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetContentStatusRoute(ContentResource _contentResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/status/[id]"));
        this.contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest httpRequest, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                ContentDetail contentDetail = contentResource.getContentDetail(oi.id());
                request.respondJson().object(contentDetail);
            }
        };
    }
}

