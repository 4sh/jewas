package fr.fsh.bbeeg;

import fr.fsh.bbeeg.routes.GetSimpleSearchContent;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.routes.SimpleHtmlRoute;
import jewas.routes.StaticResourceRoute;

/**
 * @author driccio, fcamblor
 */
public class Main {
    public static void main(String[] args) {
        final RestServer rs = RestServerFactory.createRestServer(8086);
        rs.addRoutes(
                new StaticResourceRoute(),
                new SimpleHtmlRoute("/dashboard", "dashboard/dashboard.ftl"),
                new GetSimpleSearchContent(),
                new SimpleHtmlRoute("/content/search.html", "search/search.ftl")
        ).start();
        System.out.println("Ready, if you are");
    }

}
