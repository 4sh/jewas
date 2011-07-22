package fr.fsh.bbeeg;

import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.RestServer;
import jewas.http.Route;
import jewas.http.connector.netty.NettyHttpConnector;
import jewas.routes.StaticResourceRoute;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) {
        final RestServer rs = new RestServer(new NettyHttpConnector())
                .addRoutes(
                        new StaticResourceRoute()
                );
        rs.addHandler(new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                RequestHandler match = null;
                for (Route r : rs.routes()) {
                    if ((match = r.match(request)) != null) {
                        match.onRequest(request);
                        return;
                    }
                }
            }
        })
                .bind(8086)
                .start();
        System.out.println("Ready, if you are");
    }

}
