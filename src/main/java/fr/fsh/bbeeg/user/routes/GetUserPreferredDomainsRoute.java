package fr.fsh.bbeeg.user.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.NumberObject;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.resources.UserResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author driccio
 */
public class GetUserPreferredDomainsRoute extends AbstractRoute {
    private UserResource userResource;

    public GetUserPreferredDomainsRoute(UserResource userResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/user/domains/[number]"));
        this.userResource = userResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final NumberObject numberObject = toQueryObject(parameters, NumberObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<Domain> results = new ArrayList<Domain>();
                userResource.fetchDomains(results, numberObject.number(), null); // TODO use the current connected user.
                request.respondJson().object(results, new TypeToken<List<Domain>>(){}.getType());
            }
        };
    }
}
