package fr.fsh.bbeeg.domain.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.domain.resources.DomainResource;
import fr.fsh.bbeeg.domain.resources.DomainSearchResult;
import jewas.http.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 28/07/11
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class GetPopularDomainRoute extends AbstractRoute {

    public GetPopularDomainRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/domain/popular/[number]"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(DomainResource.getPopularDomain(qo), new TypeToken<List<DomainSearchResult>>(){}.getType());
            }
        };
    }
}