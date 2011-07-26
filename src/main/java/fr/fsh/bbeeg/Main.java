package fr.fsh.bbeeg;

import fr.fsh.bbeeg.routes.GetDashboardRoute;
import fr.fsh.bbeeg.routes.GetSearchRoute;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.routes.StaticResourceRoute;

/**
 * @author driccio, fcamblor
 */
public class Main {
    public static void main(String[] args) {
        final RestServer rs = RestServerFactory.createRestServer(8086);
        rs.addRoutes(
                new StaticResourceRoute(),
                new GetDashboardRoute(),
                new GetSearchRoute()
        ).start();
        System.out.println("Ready, if you are");
    }

}
