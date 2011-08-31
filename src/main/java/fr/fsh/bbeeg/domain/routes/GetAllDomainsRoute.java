package fr.fsh.bbeeg.domain.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.domain.resources.DomainResource;
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
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 28/07/11
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class GetAllDomainsRoute extends AbstractRoute {
    private DomainResource domainResource;

    public GetAllDomainsRoute(DomainResource domainResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/domain/all"));
        this.domainResource = domainResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<Domain> results = new ArrayList<Domain>();
                domainResource.fetchAllDomains(results);
                request.respondJson().object(results, new TypeToken<List<Domain>>(){}.getType());
            }
        };
    }
}