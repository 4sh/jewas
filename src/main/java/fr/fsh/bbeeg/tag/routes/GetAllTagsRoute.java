package fr.fsh.bbeeg.tag.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.tag.pojos.Tag;
import fr.fsh.bbeeg.tag.resources.TagResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carmarolli
 * Date: 04/10/11
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public class GetAllTagsRoute extends AbstractRoute {

    private TagResource tagResource;

    public GetAllTagsRoute(TagResource tagResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/tags/all"));
        this.tagResource = tagResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<Tag> results = new ArrayList<Tag>();
                tagResource.fetchAllTags(results);
                request.respondJson().object(results, new TypeToken<List<Tag>>(){}.getType());
            }
        };
    }
}
