package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;


public class ReIndexContentsRoute extends AbstractRoute {
    private ContentResource contentResource;

    public ReIndexContentsRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/reindex"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                contentResource.reIndexAllInElasticSearch();
            }
        };
    }
}
