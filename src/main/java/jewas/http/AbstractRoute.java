package jewas.http;

public abstract class AbstractRoute implements Route {
	private final HttpMethodMatcher methodMatcher;
	private final UriPathMatcher pathMatcher;
	
	public AbstractRoute(HttpMethodMatcher methodMatcher,
			UriPathMatcher pathMatcher) {
		super();
		this.methodMatcher = methodMatcher;
		this.pathMatcher = pathMatcher;
	}

	@Override
	public RequestHandler match(HttpRequest request) {
		if (methodMatcher.match(request.method())) {
			Parameters pathParams = pathMatcher.match(request.path());
			if (pathParams != null) {
				return onMatch(request, request.parameters().union(pathParams));
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
