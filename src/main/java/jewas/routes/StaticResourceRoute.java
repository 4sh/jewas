package jewas.routes;

import jewas.http.*;
import jewas.http.impl.StaticResourceRequestHandler;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 7/22/11
 * Time: 16:10 AM
 */
public class StaticResourceRoute extends AbstractRoute {

    public StaticResourceRoute() {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/public/[path]"));
    }


    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new StaticResourceRequestHandler(parameters.val("path"));
    }
}
