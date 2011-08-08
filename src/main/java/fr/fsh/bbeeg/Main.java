package fr.fsh.bbeeg;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.fsh.bbeeg.common.CliOptions;
import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.content.routes.*;
import fr.fsh.bbeeg.domain.routes.GetPopularDomainRoute;
import fr.fsh.bbeeg.security.routes.PostConnectionRoute;
import fr.fsh.bbeeg.user.routes.GetLastConnectionDateRoute;
import fr.fsh.bbeeg.user.routes.GetUserInformationsRoute;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.routes.RedirectRoute;
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
        CliOptions options = new CliOptions();
        try {
            JCommander jcommander = new JCommander(options, args);
        }catch(ParameterException e){
            System.err.println(e.getMessage());
            new JCommander(options).usage();
            System.exit(-1);
        }

        // Registering cli options
        BBEEGConfiguration.INSTANCE.cliOptions(options);

        final RestServer rs = RestServerFactory.createRestServer(options.httpPort());
        rs.addRoutes(
                new RedirectRoute("/", "/dashboard/dashboard.html"),
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
                new GetUserInformationsRoute(),
                new SimpleHtmlRoute("/login.html", "login.ftl"),
                new PostConnectionRoute(),
                new GetContentTypeRoute(),
                new GetContentCriteriasRoute(),
                new GetAdvancedSearchContent(),
                new GetViewContentRoute()
        ).start();
        System.out.println("Ready, if you dare");
    }

}
