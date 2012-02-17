package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.common.resources.ObjectId;
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
public class CreateContentOfTextRoute extends AbstractRoute {
    private ContentResource contentResource;

    public CreateContentOfTextRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/[id]/content/text"));
        contentResource = _contentResource;
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
        final ObjectId qo = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                TextQueryObject tqo = toContentObject(bodyParameters, TextQueryObject.class);
                String fileName = TempFiles.store(tqo.text());
                contentResource.updateContentOfContent(qo.id(), fileName, false);
                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
