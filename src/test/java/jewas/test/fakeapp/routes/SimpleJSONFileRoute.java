package jewas.test.fakeapp.routes;

import jewas.http.*;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/18/11
 * Time: 9:09 AM
 */
public class SimpleJSONFileRoute extends AbstractRoute {

    public SimpleJSONFileRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/root/toUpperCase/[stringToConvert]"));
    }

    public static class QueryObject {
        private String stringToConvert;
        public String stringToConvert(){
            return stringToConvert;
        }
        public QueryObject stringToConvert(String s){
            this.stringToConvert = s;
            return this;
        }
    }

    public static class UpperCasedJSON {
        private String convertedString;
        public String convertedString(){
            return convertedString;
        }
        public UpperCasedJSON convertedString(String s){
            this.convertedString = s;
            return this;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryObject qo = toQueryObject(parameters, QueryObject.class);
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                UpperCasedJSON result = new UpperCasedJSON();
                result.convertedString(qo.stringToConvert().toUpperCase());
                request.respondJson().object(result);
            }
        };
    }
}
