package jewas.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRoute implements Route {
	private final HttpMethodMatcher methodMatcher;
	private final UriPathMatcher pathMatcher;
	private final static Pattern URI_PATTERN = Pattern.compile("^(/|(?:/([\\w\\.\\_\\-\\d]+))*)/*$");

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

	protected abstract RequestHandler onMatch(HttpRequest request, Parameters parameters);
}
