package fr.fsh.bbeeg.user.routes;

import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.json.Json;
import org.joda.time.DateMidnight;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 25/07/11
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class GetLastConnectionDateRoute extends AbstractRoute {

    public GetLastConnectionDateRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/user/lastConnectionDate"));
    }

    public class ResultObject {
        public DateMidnight date;

        public ResultObject(DateMidnight date) {
            this.date = date;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(Json.instance().toJsonString(new ResultObject(new DateMidnight())));
            }
        };
    }
}
