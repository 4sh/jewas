package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;
import jewas.json.Json;

/**
 * @author fcamblor
 */
public class CreateContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public CreateContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/content"));
        contentResource = _contentResource;
    }

    public static class QueryObject {
        private String type;
        private String contentDetail;

        public QueryObject type(String _type){
            this.type = _type;
            return this;
        }

        public String type(){
            return this.type;
        }

        public QueryObject contentDetail(String _contentDetail){
            this.contentDetail = _contentDetail;
            return this;
        }

        public String contentDetail(){
            return this.contentDetail;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                QueryObject qo = toContentObject(bodyParameters, QueryObject.class);

                ContentDetail contentDetail = (ContentDetail) Json.instance().fromJsonString(qo.contentDetail(), ContentDetail.class);
                contentDetail.header().type(ContentType.valueOf(qo.type()));

                Long id = contentResource.createContent(contentDetail);
                request.respondJson().object(new ObjectId().id(id));
            }
        };
    }
}
