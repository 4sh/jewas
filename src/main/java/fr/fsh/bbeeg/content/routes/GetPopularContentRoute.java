package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import fr.fsh.bbeeg.content.resources.ContentSearchResult;
import jewas.http.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetPopularContentRoute extends AbstractRoute {

    public GetPopularContentRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/popular/[number]"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(ContentResource.getPopularContent(qo), new TypeToken<List<ContentSearchResult>>(){}.getType());
            }
        };
    }
}
