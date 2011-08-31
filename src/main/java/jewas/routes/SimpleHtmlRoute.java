package jewas.routes;

import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;

/**
 * @author fcamblor
 */
public class SimpleHtmlRoute extends AbstractRoute {

    private String templatePath;

    public SimpleHtmlRoute(String uri, String templatePath) {
        this(HttpMethodMatcher.ALL, new PatternUriPathMatcher(uri), templatePath);
    }

    public SimpleHtmlRoute(HttpMethodMatcher methodMatcher, UriPathMatcher pathMatcher, String templatePath) {
        super(methodMatcher, pathMatcher);
        this.templatePath = templatePath;
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondHtml().content(Templates.process(templatePath, null));
            }
        };
    }
}
