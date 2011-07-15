package jewas.http;

public interface HttpConnector {
	void bind(int port);
	void start();
    void stop();
	void addHandler(RequestHandler requestHandler);
}
