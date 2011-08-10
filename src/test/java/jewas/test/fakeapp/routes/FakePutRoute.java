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

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(new Result().result("putOk"));
            }
        };
    }
}
