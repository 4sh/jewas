package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;
import jewas.json.Json;

import java.util.Date;

/**
 * @author fcamblor
 */
public class EditContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public EditContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/content/[id]"));
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
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {

            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                QueryObject qo = toContentObject(bodyParameters, QueryObject.class);

                ContentDetail contentDetail = (ContentDetail) Json.instance().fromJsonString(qo.contentDetail(), ContentDetail.class);
                contentDetail.header().type(fr.fsh.bbeeg.content.pojos.ContentType.valueOf(qo.type()));
                contentDetail.header().id(oi.id());
                contentDetail.header().lastModificationDate(new Date());

                contentResource.updateContent(contentDetail);
                request.respondJson().object(new ObjectId().id(contentDetail.header().id()));
            }
        };
    }
}
