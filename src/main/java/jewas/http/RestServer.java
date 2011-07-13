package jewas.http;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



public class RestServer {
	private HttpConnector connector;
	private final List<Route> routes = new CopyOnWriteArrayList<Route>();
	
	public RestServer(HttpConnector connector) {
		super();
		this.connector = connector;
	}

	public RestServer start() {
		connector.start();
		return this;
	}


	public RestServer bind(int port) {
		connector.bind(port);
		return this;
	}

	public RestServer addHandler(RequestHandler requestHandler) {
		connector.addHandler(requestHandler);
		return this;
	}

	public List<Route> routes() {
		return routes;
	}

	public RestServer addRoutes(Route... routes) {
		this.routes.addAll(Arrays.asList(routes));
		return this;
	}
}
