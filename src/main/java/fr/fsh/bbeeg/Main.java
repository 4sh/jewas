package fr.fsh.bbeeg;

import fr.fsh.bbeeg.content.routes.GetAddedContentRoute;
import fr.fsh.bbeeg.content.routes.GetAuthorContentRoute;
import fr.fsh.bbeeg.content.routes.GetViewedContentRoute;
import fr.fsh.bbeeg.content.routes.GetPopularContentRoute;
import fr.fsh.bbeeg.content.routes.GetSimpleSearchContent;
import fr.fsh.bbeeg.content.routes.GetTotalNumberOfContentRoute;
import fr.fsh.bbeeg.domain.routes.GetPopularDomainRoute;
import fr.fsh.bbeeg.user.routes.GetLastConnectionDateRoute;
import fr.fsh.bbeeg.user.routes.GetUserInformationsRoute;
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
                new GetAddedContentRoute(),
                new GetViewedContentRoute(),
                new GetPopularContentRoute(),
                new GetLastConnectionDateRoute(),
                new GetTotalNumberOfContentRoute(),
                new GetAuthorContentRoute(),
                new GetPopularDomainRoute(),
                new SimpleHtmlRoute("/user/profile.html", "user/profile.ftl"),
                new GetUserInformationsRoute()
        ).start();
        System.out.println("Ready, if you dare");
    }

}
