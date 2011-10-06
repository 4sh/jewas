package fr.fsh.bbeeg.tag.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.tag.pojos.Tag;
import fr.fsh.bbeeg.tag.resources.TagResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carmarolli
 * Date: 06/10/11
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class GetPopularTagRoute extends AbstractRoute {
    private TagResource tagResource;

    public GetPopularTagRoute(TagResource tagResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/tag/popular/[number]"));
        this.tagResource = tagResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<Tag> results = new ArrayList<Tag>();
                tagResource.getPopularTags(results, qo);
                request.respondJson().object(results, new TypeToken<List<Tag>>() {
                }.getType());
            }
        };
    }
}
