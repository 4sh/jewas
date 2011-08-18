package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.Author;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetAuthorContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetAuthorContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/author/[ordering]/[number]"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(contentResource.getAuthor(qo), new TypeToken<List<Author>>(){}.getType());
            }
        };
    }
}
