package jewas.http;

public interface Route {
	public RequestHandler match(HttpRequest request);
}
