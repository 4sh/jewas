package jewas.routes;

import jewas.http.*;
import jewas.http.impl.FileRequestHandler;

/**
 * Route which will serve a set of static resources from a parameterized url where
 * path for the file will be extracted.
 * File will be loaded from the classpath.
 */
public class StaticResourcesRoute extends AbstractRoute {

    private String pathPrefix;

    /**
     * @param urlPrefix The url prefix for static resource paths
     */
    public StaticResourcesRoute(String urlPrefix){
        this(urlPrefix, "");
    }

    /**
     * @param urlPrefix The url prefix for static resource paths
     * @param pathPrefix A prefix which will be appended to path extracted from the url. Can be null (resource
     * will then be looked starting at the classpath root)
     */
    public StaticResourcesRoute(String urlPrefix, String pathPrefix) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher(urlPrefix+(urlPrefix.endsWith("/")?"":"/")+"[path]"));
        this.pathPrefix = pathPrefix;
    }


    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new FileRequestHandler(pathPrefix+parameters.val("path"));
    }
}
