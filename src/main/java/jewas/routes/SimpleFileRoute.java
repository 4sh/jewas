package jewas.routes;

import jewas.http.*;
import jewas.template.Templates;
import jewas.util.file.Files;

import java.io.IOException;

/**
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
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                try {
                    request.respondFile().file(Files.getInputStreamFromPath(filepath));
                } catch (IOException e) {
                    request.respondError(HttpStatus.NOT_FOUND);
                }
            }
        };
    }
}
