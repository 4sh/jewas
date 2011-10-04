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
public class UploadRoute extends AbstractRoute {
    private ContentResource contentResource;

    public UploadRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/upload/[type]"));
        contentResource = _contentResource;
    }

    public static class QueryObject {
        private String id;
        private String type;

        public QueryObject id(String _id){
            this.id = _id;
            return this;
        }

        public String id(){
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

    public static class SuccessFileUploadObject extends SuccessObject {
        private String fileId;

        public SuccessFileUploadObject fileId(String _fileId){
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
                String fileName;

                if (ContentType.TEXT.name().equals(qo.type())) {
                    TextQueryObject fqo = toContentObject(bodyParameters, TextQueryObject.class);
                    fileName = contentResource.temporaryUpdateContentOfContent(fqo.text());
                } else {
                    FileQueryObject fqo = toContentObject(bodyParameters, FileQueryObject.class);
                    fileName = contentResource.temporaryUpdateContentOfContent(fqo.file(), fqo.extension());
                }

                SuccessFileUploadObject sfuo = new SuccessFileUploadObject();
                sfuo.success(true);
                sfuo.fileId(fileName);

                request.respondJson().object(sfuo);
            }
        };
    }
}
