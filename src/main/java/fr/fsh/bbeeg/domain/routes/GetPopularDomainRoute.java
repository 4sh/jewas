package fr.fsh.bbeeg.domain.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.domain.resources.DomainResource;
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
 * Date: 28/07/11
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class GetPopularDomainRoute extends AbstractRoute {
    private DomainResource domainResource;

    public GetPopularDomainRoute(DomainResource domainResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/domain/popular/[number]"));
        this.domainResource = domainResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final LimitedOrderedQueryObject qo = toQueryObject(parameters, LimitedOrderedQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(domainResource.getPopularDomain(qo), new TypeToken<List<Domain>>(){}.getType());
            }
        };
    }
}