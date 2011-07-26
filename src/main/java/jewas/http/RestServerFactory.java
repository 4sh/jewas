package jewas.http;

import jewas.http.connector.netty.NettyHttpConnector;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/15/11
 * Time: 1:47 PM
 */
public class RestServerFactory {

    /**
     * Create a new rest server with a default behaviour
     * browsing every routes
     *
     * @param portNumber Port number the rest server will be listening to
     */
    public static RestServer createRestServer(int portNumber) {

        final RestServer rs = new RestServer(new NettyHttpConnector());
        rs.addHandler(new RequestHandler() {
            public void onRequest(HttpRequest request) {
                RequestHandler match = null;
                for (Route r : rs.routes()) {
                    if ((match = r.match(request)) != null) {
                        match.onRequest(request);
                        return;
                    }
                }
                request.respondError(HttpStatus.NOT_FOUND);
            }
        }).bind(portNumber);
        return rs;
    }
}
