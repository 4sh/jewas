package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetAddedContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetAddedContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/added/[ordering]/[number]"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(contentResource.getAddedContent(qo), new TypeToken<List<ContentHeader>>(){}.getType());
            }
        };
    }
}
