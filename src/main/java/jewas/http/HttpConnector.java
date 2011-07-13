package jewas.http;

public interface HttpConnector {
	void bind(int port);
	void start();
	void addHandler(RequestHandler requestHandler);
}
