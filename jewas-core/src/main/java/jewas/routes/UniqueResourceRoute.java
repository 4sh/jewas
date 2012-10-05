package jewas.routes;

import jewas.configuration.JewasConfiguration;
import jewas.http.*;
import jewas.resources.Resource;

import java.io.File;

/**
 * Route which will serve a unique resource from a given url.
 * Resource will be loaded either from the classpath or the filesystem
 * @author fcamblor
 */
public class UniqueResourceRoute extends StaticResourcesRoute {

    private Resource resource;

    public UniqueResourceRoute(String uri, Resource resource) {
        this(uri, resource, JewasConfiguration.cachedResourcesDirectory());
    }

    public UniqueResourceRoute(String uri, Resource resource, File cachedResourcesFileSystemRootDir) {
        this(HttpMethodMatcher.ALL, new PatternUriPathMatcher(uri), resource, cachedResourcesFileSystemRootDir);
    }

    public UniqueResourceRoute(HttpMethodMatcher methodMatcher, UriPathMatcher pathMatcher, Resource resource,
                               File cachedResourcesFileSystemRootDir) {
        super(methodMatcher, pathMatcher, null, cachedResourcesFileSystemRootDir);
        this.resource = resource;
    }

    @Override
    protected Resource createResource(HttpRequest request, Parameters parameters){
        return resource;
    }
}
