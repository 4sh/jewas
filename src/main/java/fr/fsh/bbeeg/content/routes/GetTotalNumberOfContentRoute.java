package fr.fsh.bbeeg.content.routes;

import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.json.Json;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetTotalNumberOfContentRoute extends AbstractRoute {

    public GetTotalNumberOfContentRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/total"));
    }

    public class ResultObject {
        public Integer number;

        public ResultObject(Integer number) {
            this.number = number;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(Json.instance().toJsonString(new ResultObject(367)));
            }
        };
    }
}