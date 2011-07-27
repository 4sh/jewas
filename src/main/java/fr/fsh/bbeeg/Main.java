package fr.fsh.bbeeg;

import fr.fsh.bbeeg.routes.GetDashboardRoute;
import fr.fsh.bbeeg.routes.GetSearchRoute;
import fr.fsh.bbeeg.routes.GetLastAddedContentRoute;
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
        final RestServer rs = RestServerFactory.createRestServer(8086);
        rs.addRoutes(
                new StaticResourceRoute(),
                new SimpleHtmlRoute("/dashboard/dashboard.html", "dashboard/dashboard.ftl"),
                new GetSimpleSearchContent(),
                new SimpleHtmlRoute("/content/search.html", "searh/search.ftl"),
                new GetLastAddedContentRoute(),
                new GetLastViewedContentRoute(),
                new GetMostPopularContentRoute(),
                new GetLastConnectionDateRoute()
        ).start();
        System.out.println("Ready, if you are");
    }

}
