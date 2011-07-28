package fr.fsh.bbeeg.domain.routes;

import fr.fsh.bbeeg.content.resources.ContentQueryObject;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.json.Json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public class ResultObject {
        public String text;
        public BigDecimal weight;
        public String url;

        public ResultObject(String text, BigDecimal weight, String url) {
            this.text = text;
            this.url = url;
            this.weight = weight;
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
                    list.add(new ResultObject("Domain" + i, new BigDecimal(new Random().nextInt(10)), ""));
                }

                request.respondJson().object(Json.instance().toJsonString(list));
            }
        };
    }
}