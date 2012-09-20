package jewas.routes;

import jewas.http.*;

import java.io.File;

/**
 * Route which will serve a unique file resource from a given url.
 * File will be loaded from the classpath.
 * @author fcamblor
 */
public class SimpleFileRoute extends StaticResourcesRoute {

    private String filepath;

    public SimpleFileRoute(String uri, String filepath, File cachedResourcesFileSystemRootDir) {
        this(HttpMethodMatcher.ALL, new PatternUriPathMatcher(uri), filepath, cachedResourcesFileSystemRootDir);
    }

    public SimpleFileRoute(HttpMethodMatcher methodMatcher, UriPathMatcher pathMatcher, String filepath,
                           File cachedResourcesFileSystemRootDir) {
        super(methodMatcher, pathMatcher, null, cachedResourcesFileSystemRootDir);
        this.filepath = filepath;
    }

    @Override
    protected String currentResourcePath(HttpRequest request, Parameters parameters){
        return filepath;
    }
}
