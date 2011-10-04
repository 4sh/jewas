package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class CreateContentOfContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public CreateContentOfContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/[contentId]/content/[fileId]"));
        contentResource = _contentResource;
    }

    public static class QueryObject {
        private Long contentId;
        private String fileId;

        public QueryObject contentId(Long _contentId){
            this.contentId = _contentId;
            return this;
        }

        public Long contentId(){
            return this.contentId;
        }


        public QueryObject fileId(String _fileId){
            this.fileId = _fileId;
            return this;
        }

        public String fileId(){
            return this.fileId;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryObject qo = toQueryObject(parameters, QueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                contentResource.updateContentOfContent(qo.contentId(), qo.fileId());

                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
