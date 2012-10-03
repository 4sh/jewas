package jewas.http;

import jewas.http.data.BodyParameters;
import jewas.http.data.FormBodyParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRoute implements Route {

    private final static Logger logger = LoggerFactory.getLogger(AbstractRoute.class);

    private final HttpMethodMatcher methodMatcher;
    private final UriPathMatcher pathMatcher;
    private final static Pattern URI_PATTERN = Pattern.compile("^(/|(?:/([\\w\\.\\_\\-\\d@]+))*)/*$");

    public AbstractRoute(HttpMethodMatcher methodMatcher, String path) {
        this(methodMatcher, new PatternUriPathMatcher(path));
    }

    public AbstractRoute(HttpMethodMatcher methodMatcher,
                         UriPathMatcher pathMatcher) {
        super();
        this.methodMatcher = methodMatcher;
        this.pathMatcher = pathMatcher;
    }

    @Override
    public RequestHandler match(HttpRequest request) {
        if (methodMatcher.match(request.method())) {
            Matcher m = URI_PATTERN.matcher(request.path());
            if (m.matches()) {
                Parameters pathParams = pathMatcher.match(m.group(1));
                if (pathParams != null) {
                    logger.debug("Matched route: " + this.getClass().getSimpleName() + " on path : " + m.group(1));
                    return onMatch(request, request.parameters().union(pathParams));
                }
            }
        }
        return null;
    }

    protected <T> T toQueryObject(Parameters parameters,
                                  Class<T> clazz) {
        return QueryObjects.toQueryObject(parameters, clazz);
    }

    protected <T> T toContentObject(FormBodyParameters parameters,
                                    Class<T> clazz) {
        return QueryObjects.toContentObject(parameters, clazz);
    }

    protected <T> T toContentObject(BodyParameters parameters,
                                    Class<T> clazz) {
        return QueryObjects.toContentObject((FormBodyParameters) parameters, clazz);
    }

    protected abstract RequestHandler onMatch(HttpRequest request, Parameters parameters);
}
