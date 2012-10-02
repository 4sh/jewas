package jewas.routes;

import jewas.http.*;
import jewas.http.impl.ResourceRequestHandler;
import jewas.resources.ClasspathResource;
import jewas.resources.Resource;

import java.io.File;

/**
 * Route which will serve a set of static resources from a parameterized url where
 * path for the file will be extracted.
 * File will be loaded from the classpath.
 */
public class StaticResourcesRoute extends AbstractRoute {

    /* Path that will be appended to the beginning of every requested paths */
    private String pathPrefix;
    /* Directory that will be used to extract files from jar to the filesystem the first
    time it is accessed */
    private File cachedResourcesFileSystemRootDir;

    /**
     * @param urlPrefix The url prefix for static resource paths
     * @param cachedResourcesFileSystemRootDir Path where cached file resources will be extracted to be served
     * more fastly. Moreover, it fixes some
     */
    public StaticResourcesRoute(String urlPrefix, File cachedResourcesFileSystemRootDir){
        this(urlPrefix, "", cachedResourcesFileSystemRootDir);
    }

    /**
     * @param urlPrefix The url prefix for static resource paths
     * @param pathPrefix A prefix which will be appended to path extracted from the url. Can be null (resource
     * will then be looked starting at the classpath root)
     * @param cachedResourcesFileSystemRootDir Path where cached file resources will be extracted to be served
     * more fastly. Moreover, it fixes some
     */
    public StaticResourcesRoute(String urlPrefix, String pathPrefix, File cachedResourcesFileSystemRootDir) {
        this(HttpMethodMatcher.GET, new PatternUriPathMatcher(urlPrefix+(urlPrefix.endsWith("/")?"":"/")+"[path]"),
                pathPrefix, cachedResourcesFileSystemRootDir);
    }

    /**
     * @param pathPrefix A prefix which will be appended to path extracted from the url. Can be null (resource
     * will then be looked starting at the classpath root)
     * @param cachedResourcesFileSystemRootDir Path where cached file resources will be extracted to be served
     * more fastly. Moreover, it fixes some
     */
    public StaticResourcesRoute(HttpMethodMatcher matcher, UriPathMatcher pathMatcher,
                                String pathPrefix, File cachedResourcesFileSystemRootDir) {
        super(matcher, pathMatcher);
        this.pathPrefix = pathPrefix;
        this.cachedResourcesFileSystemRootDir = cachedResourcesFileSystemRootDir;
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new ResourceRequestHandler(this.cachedResourcesFileSystemRootDir, createResource(request, parameters));
    }

    protected Resource createResource(HttpRequest request, Parameters parameters){
        return new ClasspathResource(pathPrefix+parameters.val("path"));
    }
}
