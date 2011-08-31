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
import jewas.http.impl.AbstractRequestHandler;
import jewas.json.Json;

import java.nio.ByteBuffer;

/**
 * @author fcamblor
 */
public class CreateTextContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public CreateTextContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/content/text"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                ByteBuffer bytebuff = request.content();
                byte[] bytearray = new byte[bytebuff.remaining()];
                bytebuff.get(bytearray);
                String text = new String(bytearray);

                ContentDetail contentDetail = (ContentDetail) Json.instance().fromJsonString(text, ContentDetail.class);
                contentDetail.header().type(ContentType.TEXT);

                Long id = contentResource.createContent(contentDetail);
                request.respondJson().object(new ObjectId().id(id));
            }

        };
    }
}
