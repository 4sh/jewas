package jewas.test.fakeapp.routes;

import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.StaticResourceRequestHandler;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 7/20/11
 * Time: 10:19 AM
 */
public class StaticResourceRoute extends AbstractRoute {

    public StaticResourceRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/public/[path]"));
    }


    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new StaticResourceRequestHandler(parameters.val("path"));
    }
}
