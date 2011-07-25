package fr.fsh.bbeeg.routes;

import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.template.Templates;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 25/07/11
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class GetDashboardRoute extends AbstractRoute {

    public GetDashboardRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/dashboard"));
    }


    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondHtml().content(Templates.process("dashboard/dashboard.ftl", null));
            }
        };
    }
}
