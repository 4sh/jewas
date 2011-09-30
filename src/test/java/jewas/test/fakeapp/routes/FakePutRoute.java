package jewas.test.fakeapp.routes;

import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;
import jewas.test.fakeapp.routes.model.Result;

/**
 * @author fcamblor
 */
public class FakePutRoute extends AbstractRoute {

    public FakePutRoute(){
        super(HttpMethodMatcher.PUT, new PatternUriPathMatcher("/putThing"));
    }

    public static class BodyParam {
        private String id;
        public BodyParam id(String _id){
            this.id = _id;
            return this;
        }
        public String id(){
            return this.id;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                BodyParam params = toContentObject(bodyParameters, BodyParam.class);
                if(params.id() == null){
                    request.respondJson().object(new Result().result("putOk"));
                } else {
                    request.respondJson().object(new Result().result("putOk of "+params.id()));
                }
            }
        };
    }
}
