package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.pojos.ContentTypeResultObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;

import java.util.List;

/**
 * @author driccio
 */
public class GetContentTypeRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetContentTypeRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/type/[ordering]/[number]"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(contentResource.getContentType(qo), new TypeToken<List<ContentTypeResultObject>>(){}.getType());
            }
        };
    }
}
