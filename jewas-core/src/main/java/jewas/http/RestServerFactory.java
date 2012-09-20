package jewas.http;

import jewas.http.connector.netty.NettyHttpConnector;
import jewas.http.impl.AbstractRequestHandler;
import jewas.http.impl.AbstractRequestHandlerDelegate;

import java.util.ArrayList;
import java.util.List;

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
        rs.addHandler(new AbstractRequestHandlerDelegate() {
            @Override
            public List<RequestHandler> findDelegatesFor(HttpRequest request) {
                RequestHandler match = null;
                for (Route r : rs.routes()) {
                    if ((match = r.match(request)) != null) {
                        // At first match, we stop !!
                        // It will imply routes ordering is important !
                        List<RequestHandler> matchingHandler = new ArrayList<RequestHandler>();
                        matchingHandler.add(match);
                        return matchingHandler;
                    }
                }
                request.respondError(HttpStatus.NOT_FOUND);
                return null;
            }
        }).bind(portNumber);
        return rs;
    }
}
