package fr.fsh.bbeeg.user.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.user.pojos.User;
import fr.fsh.bbeeg.user.resources.UserResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

import java.util.ArrayList;
import java.util.List;

public class GetUserAuthorRoute extends AbstractRoute {
    private UserResource userResource;

    public GetUserAuthorRoute(UserResource _userResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/users/authors/[ordering]/[number]"));
        userResource = _userResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<User> authors = new ArrayList<User>();
                userResource.fetchAuthors(authors, qo);
                request.respondJson().object(authors, new TypeToken<List<User>>() {
                }.getType());
            }
        };
    }
}
