package jewas.test.fakeapp.routes;

import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;
import jewas.test.fakeapp.routes.model.Result;

/**
 * @author fcamblor
 */
public class FakeDeleteRoute extends AbstractRoute {

    public FakeDeleteRoute(){
        super(HttpMethodMatcher.DELETE, new PatternUriPathMatcher("/deleteThing"));
    }

    public static class QueryParam {
        private String id;
        public QueryParam id(String _id){
            this.id = _id;
            return this;
        }
        public String id(){
            return this.id;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryParam param = toQueryObject(parameters, QueryParam.class);
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                if(param.id() == null){
                    request.respondJson().object(new Result().result("deleteOk"));
                } else {
                    request.respondJson().object(new Result().result("deleteOk of "+param.id()));
                }
            }
        };
    }
}
