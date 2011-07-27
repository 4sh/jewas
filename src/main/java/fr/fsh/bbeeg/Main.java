package fr.fsh.bbeeg;

import fr.fsh.bbeeg.content.routes.GetLastAddedContentRoute;
import fr.fsh.bbeeg.content.routes.GetLastViewedContentRoute;
import fr.fsh.bbeeg.content.routes.GetMostPopularContentRoute;
import fr.fsh.bbeeg.content.routes.GetSimpleSearchContent;
import fr.fsh.bbeeg.user.routes.GetLastConnectionDateRoute;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.routes.SimpleHtmlRoute;
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
                new SimpleHtmlRoute("/content/search.html", "search/search.ftl"),
                new GetLastAddedContentRoute(),
                new GetLastViewedContentRoute(),
                new GetMostPopularContentRoute(),
                new GetLastConnectionDateRoute()
        ).start();
        System.out.println("Ready, if you are");
    }

}
