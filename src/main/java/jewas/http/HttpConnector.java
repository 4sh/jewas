package jewas.http;

public interface HttpConnector {
	void bind(int port);

    /**
     * @throws AddressAlreadyInUseException if another instance is already running while starting the connector
     */
	void start();
    void stop();
	void addHandler(RequestHandler requestHandler);
}
