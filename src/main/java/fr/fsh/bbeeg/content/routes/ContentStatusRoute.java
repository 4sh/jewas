package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;

/**
 * @author driccio
 */
public class ContentStatusRoute extends AbstractRoute {
    private ContentResource contentResource;

    public ContentStatusRoute(ContentResource _contentResource) {
        super(HttpMethodMatcher.ALL, new PatternUriPathMatcher("/content/[id]/status/[status]"));
        this.contentResource = _contentResource;
    }

    public static class ContentStatusQueryObject {
        private Long id;
        private String status;

        public ContentStatusQueryObject id(Long _id){
            this.id = _id;
            return this;
        }

        public Long id(){
            return this.id;
        }

        public ContentStatusQueryObject status(String _status){
            this.status = _status;
            return this;
        }

        public String status(){
            return this.status;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest httpRequest, Parameters parameters) {
        final ContentStatusQueryObject csqo = toQueryObject(parameters, ContentStatusQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // TODO: to complete !
                contentResource.updateContentStatus(csqo.id(), ContentStatus.valueOf(csqo.status()));
                request.respondJson().object("Ok");
            }
        };
    }
}
