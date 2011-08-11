package jewas.routes;

import jewas.http.*;
import jewas.http.impl.FileRequestHandler;
import jewas.template.Templates;
import jewas.util.file.Files;

import java.io.IOException;

/**
 * Route which will serve a unique file resource from a given url.
 * File will be loaded from the classpath.
 * @author fcamblor
 */
public class SimpleFileRoute extends AbstractRoute {

    private String filepath;

    public SimpleFileRoute(String uri, String filepath) {
        this(HttpMethodMatcher.ALL, new PatternUriPathMatcher(uri), filepath);
    }

    public SimpleFileRoute(HttpMethodMatcher methodMatcher, UriPathMatcher pathMatcher, String filepath) {
        super(methodMatcher, pathMatcher);
        this.filepath = filepath;
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new FileRequestHandler(filepath);
    }
}
