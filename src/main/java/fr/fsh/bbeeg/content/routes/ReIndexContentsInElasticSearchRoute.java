package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;


public class ReIndexContentsInElasticSearchRoute extends AbstractRoute {
    private ContentResource contentResource;

    public ReIndexContentsInElasticSearchRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/reindex"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                contentResource.reIndexAllInElasticSearch();
                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
