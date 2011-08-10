package jewas.test.fakeapp.routes;

import jewas.http.*;

/**
 * @author fcamblor
 */
public class FakePutRoute extends AbstractRoute {

    public FakePutRoute(){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/putThing"));
    }

    public static class Result {
        private String result;
        public Result result(String _result){
            this.result = _result;
            return this;
        }
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
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                if(param.id() == null){
                    request.respondJson().object(new Result().result("putOk"));
                } else {
                    request.respondJson().object(new Result().result("putOk of "+param.id()));
                }
            }
        };
    }
}
