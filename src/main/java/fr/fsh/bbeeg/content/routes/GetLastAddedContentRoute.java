package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.resources.ContentQueryObject;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class GetLastAddedContentRoute extends AbstractRoute {

    public GetLastAddedContentRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/lastAdded"));
    }

    public class ResultObject {
        public String name;

        public ResultObject(String name) {
            this.name = name;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ContentQueryObject qo = toQueryObject(parameters, ContentQueryObject.class);

        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<ResultObject> list = new ArrayList<ResultObject>();

                for (int i = 0; i < qo.number(); i++) {
                    list.add(new ResultObject("Item" + i));
                }

                request.respondJson().object(Json.instance().toJsonString(list));
            }
        };
    }
}
