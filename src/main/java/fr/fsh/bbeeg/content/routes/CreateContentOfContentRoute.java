package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.FileQueryObject;
import fr.fsh.bbeeg.common.resources.SuccessObject;
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

/**
 * @author driccio
 */
public class CreateContentOfContentRoute extends AbstractRoute {
    public static final String EDF_FILE_URL = "/eegFile";
    public static final String VIDEO_URL = "/video";
    private ContentResource contentResource;

    public CreateContentOfContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/content/[id]/[type]"));
        contentResource = _contentResource;
    }

    public static class QueryObject {
        private Long id;
        private String type;

        public QueryObject id(Long _id){
            this.id = _id;
            return this;
        }

        public Long id(){
            return this.id;
        }

        public QueryObject type(String _type){
            this.type = _type;
            return this;
        }

        public String type(){
            return this.type;
        }
    }

    public static class TextQueryObject {
        private String text;

        public TextQueryObject text(String _text){
            this.text = _text;
            return this;
        }

        public String text(){
            return this.text;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryObject qo = toQueryObject(parameters, QueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                if (ContentType.TEXT.name().equals(qo.type())) {
                    TextQueryObject fqo = toContentObject(bodyParameters, TextQueryObject.class);
                    contentResource.updateContentOfContent(qo.id(), fqo.text());
                } else {
                    FileQueryObject fqo = toContentObject(bodyParameters, FileQueryObject.class);

                    contentResource.updateContentOfContent(qo.id(), ContentType.valueOf(qo.type()),
                            fqo.file(), fqo.extension());
                }

                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
