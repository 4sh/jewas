package fr.fsh.bbeeg;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.fsh.bbeeg.common.CliOptions;
import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.content.routes.ContentStatusRoute;
import fr.fsh.bbeeg.content.routes.CreateContentOfContentRoute;
import fr.fsh.bbeeg.content.routes.CreateTextContentRoute;
import fr.fsh.bbeeg.content.routes.EditContentRoute;
import fr.fsh.bbeeg.content.routes.GetAddedContentRoute;
import fr.fsh.bbeeg.content.routes.GetAdvancedSearchContent;
import fr.fsh.bbeeg.content.routes.GetAuthorContentRoute;
import fr.fsh.bbeeg.content.routes.GetContentCriteriasRoute;
import fr.fsh.bbeeg.content.routes.GetContentOfContentRoute;
import fr.fsh.bbeeg.content.routes.GetContentToTreatSearchScreenRoute;
import fr.fsh.bbeeg.content.routes.GetContentTypeRoute;
import fr.fsh.bbeeg.content.routes.GetPopularContentRoute;
import fr.fsh.bbeeg.content.routes.GetSearchScreenRoute;
import fr.fsh.bbeeg.content.routes.GetSimpleSearchContent;
import fr.fsh.bbeeg.content.routes.GetTotalNumberOfContentRoute;
import fr.fsh.bbeeg.content.routes.GetUserContentsSearchScreenRoute;
import fr.fsh.bbeeg.content.routes.GetViewContentRoute;
import fr.fsh.bbeeg.content.routes.GetViewedContentRoute;
import fr.fsh.bbeeg.domain.routes.GetAllDomainsRoute;
import fr.fsh.bbeeg.domain.routes.GetPopularDomainRoute;
import fr.fsh.bbeeg.security.routes.PostConnectionRoute;
import fr.fsh.bbeeg.user.routes.GetLastConnectionDateRoute;
import fr.fsh.bbeeg.user.routes.GetUserInformationsRoute;
import fr.fsh.bbeeg.user.routes.GetUserPreferredDomainsRoute;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.routes.RedirectRoute;
import jewas.routes.SimpleFileRoute;
import jewas.routes.SimpleHtmlRoute;
import jewas.routes.StaticResourcesRoute;
import jewas.util.file.Files;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
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

        org.h2.tools.Server.createWebServer("-webPort",
                BBEEGConfiguration.INSTANCE.cliOptions().h2ServerPort()).start();
        Class.forName("org.h2.Driver");
        Connection dbInitializationConnection = DriverManager.getConnection("jdbc:h2:mem:mytest", "sa", "");
        ScriptRunner sr = new ScriptRunner(dbInitializationConnection, true, true);
        sr.runScript(new InputStreamReader(Files.getInputStreamFromPath("fr/fsh/bbeeg/bbeeg_script.sql")));

        Assembler assembler = new Assembler();

        final RestServer rs = RestServerFactory.createRestServer(options.httpPort());
        rs.addRoutes(
                new RedirectRoute("/", "/dashboard/dashboard.html"),
                // Not really a static resource (located in webapp folder) since it is provided
                // by jewas library. So it must be declared before the StaticResourcesRoute !
                new SimpleFileRoute("/public/js/jewas/jewas-forms.js", "js/jewas-forms.js"),
                new StaticResourcesRoute("/public/", "public/"),
                new SimpleHtmlRoute("/dashboard/dashboard.html", "dashboard/dashboard.ftl"),
                new GetSimpleSearchContent(assembler.contentResource()),
                new GetSearchScreenRoute(),
                new ContentStatusRoute(assembler.contentResource()),
                new GetUserContentsSearchScreenRoute(),
                new GetContentToTreatSearchScreenRoute(),
                new CreateContentOfContentRoute(assembler.contentResource()),
               // new GetCreateContentRoute(assembler.contentResource()),
                new CreateTextContentRoute(assembler.contentResource()),
                new GetContentOfContentRoute(assembler.contentResource()),
                new SimpleHtmlRoute("/content/text/create.html", "content/create-text.ftl"),
                new EditContentRoute(assembler.contentResource()),
                new GetAddedContentRoute(assembler.contentResource()),
                new GetViewedContentRoute(assembler.contentResource()),
                new GetPopularContentRoute(assembler.contentResource()),
                new GetLastConnectionDateRoute(),
                new GetTotalNumberOfContentRoute(assembler.contentResource()),
                new GetAuthorContentRoute(assembler.contentResource()),
                new GetAllDomainsRoute(assembler.domainResource()),
                new GetPopularDomainRoute(assembler.domainResource()),
                new SimpleHtmlRoute("/user/profile.html", "user/profile.ftl"),
                new GetUserInformationsRoute(),
                new SimpleHtmlRoute("/login.html", "login.ftl"),
                new PostConnectionRoute(),
                new GetContentTypeRoute(assembler.contentResource()),
                new GetContentCriteriasRoute(assembler.contentResource()),
                new GetAdvancedSearchContent(assembler.contentResource()),
                new GetViewContentRoute(assembler.contentResource()),
                new GetUserPreferredDomainsRoute(assembler.userResource())
        ).start();
        System.out.println("Ready, if you dare");
    }

}
