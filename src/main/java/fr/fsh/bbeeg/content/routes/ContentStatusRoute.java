package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;
import org.joda.time.field.AbstractReadableInstantFieldProperty;

/**
 * @author driccio
 */
public class ContentStatusRoute extends AbstractRoute {
    private ContentResource contentResource;

    public ContentStatusRoute(ContentResource _contentResource) {
        super(HttpMethodMatcher.ALL, new PatternUriPathMatcher("/content/[id]/status/[status]/[comment]"));
        this.contentResource = _contentResource;
    }

    public static class ContentStatusQueryObject {
        private Long id;
        private String status;
        private String comment;

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

        public ContentStatusQueryObject comment(String _comment){
            this.comment = _comment;
            return this;
        }

        public String comment(){
            return this.comment;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest httpRequest, Parameters parameters) {
        final ContentStatusQueryObject csqo = toQueryObject(parameters, ContentStatusQueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // TODO: to complete !
                contentResource.updateContentStatus(csqo.id(), ContentStatus.valueOf(csqo.status()), csqo.comment());
                request.respondJson().object("Ok");
            }
        };
    }
}
