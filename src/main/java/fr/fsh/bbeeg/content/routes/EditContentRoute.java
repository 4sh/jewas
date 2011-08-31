package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.data.HttpData;
import jewas.http.impl.AbstractRequestHandler;
import jewas.json.Json;

import java.nio.ByteBuffer;

/**
 * @author fcamblor
 */
public class EditContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public EditContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.POST, new PatternUriPathMatcher("/content/[id]"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // TODO: to complete !
//                ContentDetail contentDetail = new ContentDetail().header(
//                        new ContentHeader().id(oi.id())
//                                .title(request.parameters().val("title"))
//                                .description(request.parameters().val("description"))
//                );

                ByteBuffer bytebuff = request.content();
                byte[] bytearray = new byte[bytebuff.remaining()];
                bytebuff.get(bytearray);
                String text = new String(bytearray);

                ContentDetail contentDetail = (ContentDetail) Json.instance().fromJsonString(text, ContentDetail.class);
                contentDetail.header().id(oi.id());

                contentResource.updateContent(contentDetail);

                request.respondJson().object("Ok");
            }
        };
    }
}
