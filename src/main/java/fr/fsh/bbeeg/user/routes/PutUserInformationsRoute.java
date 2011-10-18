package fr.fsh.bbeeg.user.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.user.pojos.User;
import fr.fsh.bbeeg.user.resources.UserResource;
import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;

/**
 * User: carmarolli
 */
public class PutUserInformationsRoute extends AbstractRoute {

    private final UserResource userResource;

    public PutUserInformationsRoute(UserResource _userResource) {
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/user/infos/[login]"));
        this.userResource = _userResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {

        return new AbstractRequestHandler() {

            @Override
            public void onReady(HttpRequest request, BodyParameters parameters) {
                super.onReady(request, parameters);
                User user = toContentObject(parameters, User.class);
                if (user != null && !user.login().isEmpty()) {
                    userResource.updateUser(user);
                    request.respondJson().object(new SuccessObject().success(true),new TypeToken<SuccessObject>() {}.getType());
                } else {
                    request.respondJson().object(new SuccessObject().success(false),new TypeToken<SuccessObject>() {}.getType());
                }
            }
         };
     }
}

