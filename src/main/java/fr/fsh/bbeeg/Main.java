package fr.fsh.bbeeg;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.fsh.bbeeg.common.CliOptions;
import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.content.routes.*;
import fr.fsh.bbeeg.domain.routes.GetAllDomainsRoute;
import fr.fsh.bbeeg.domain.routes.GetDomainsHierarchyRoute;
import fr.fsh.bbeeg.domain.routes.GetPopularDomainRoute;
import fr.fsh.bbeeg.security.routes.GetConnectedUserRoute;
import fr.fsh.bbeeg.security.routes.GetLoginRoute;
import fr.fsh.bbeeg.security.routes.GetLogoutRoute;
import fr.fsh.bbeeg.security.routes.SecurityRoute;
import fr.fsh.bbeeg.tag.routes.GetAllTagsRoute;
import fr.fsh.bbeeg.tag.routes.GetPopularTagRoute;
import fr.fsh.bbeeg.user.routes.*;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.routes.RedirectRoute;
import jewas.routes.SimpleFileRoute;
import jewas.routes.SimpleHtmlRoute;
import jewas.routes.StaticResourcesRoute;

import java.io.IOException;
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
        try {
            BBEEGConfiguration.INSTANCE.cliOptions(options);

//            org.h2.tools.Server.createWebServer("-webPort",
  //                  BBEEGConfiguration.INSTANCE.cliOptions().h2ServerPort()).start();
  //          Class.forName("org.h2.Driver");
            Assembler assembler = new Assembler(options);

            final RestServer rs = RestServerFactory.createRestServer(options.httpPort());
            rs.addRoutes(
                new GetLoginRoute(assembler.securityResource()),
                new RedirectRoute("/", "/login.html"),
                // Not really a static resources (located in webapp folder) since it is provided
                // by jewas library. So it must be declared before the StaticResourcesRoute !
                new SimpleFileRoute("/public/js/jewas/jewas-forms.js", "js/jewas-forms.js", options.cachedStaticResourcesRootDirectory()),
                new StaticResourcesRoute("/public/", "public/", options.cachedStaticResourcesRootDirectory()),
                new ReIndexContentsInElasticSearchRoute(assembler.contentResource()),
                    new SecurityRoute(),
                new GetLogoutRoute(),
                new SimpleHtmlRoute("/dashboard/dashboard.html", "dashboard/dashboard.ftl"),
                new SimpleHtmlRoute("/home.html", "home.ftl"),
                new GetConnectedUserRoute(assembler.connectedUserResource()),
                new GetSimpleSearchContent(assembler.contentResource()),
                new GetSearchScreenRoute(),
                new UpdateContentStatusRoute(assembler.contentResource()),
                new GetContentStatusRoute(assembler.contentResource()),
                new GetUserContentsSearchScreenRoute(),
                new GetContentToTreatSearchScreenRoute(),
                new GetEegInformationsRoute(assembler.eegResource()),
                new GetEegSettingsRoute(assembler.eegResource()),
                new CreateEegSettingsRoute(assembler.eegResource()),
                new SaveEegRoute(),
                new CreateContentOfTextRoute(assembler.contentResource()),
                new CreateContentOfEegRoute(assembler.eegResource()),
                new CreateContentOfContentRoute(assembler.contentResource()),
                new CreateContentRoute(assembler.contentResource()),
                new GetContentOfContentRoute(assembler.contentResource()),
                new SimpleHtmlRoute("/content/text/create.html", "content/create-text.ftl"),
                new SimpleHtmlRoute("/content/image/create.html", "content/create-image.ftl"),
                new SimpleHtmlRoute("/content/audio/create.html", "content/create-audio.ftl"),
                new SimpleHtmlRoute("/content/video/create.html", "content/create-video.ftl"),
                new SimpleHtmlRoute("/content/document/create.html", "content/create-document.ftl"),
                new SimpleHtmlRoute("/content/eeg/create.html", "content/create-eeg.ftl"),
                new EditContentRoute(assembler.contentResource()),
                new GetAddedContentRoute(assembler.contentResource()),
                new GetViewedContentRoute(assembler.contentResource()),
                new GetPopularContentRoute(assembler.contentResource()),
                new GetLastConnectionDateRoute(),
                new GetTotalNumberOfContentRoute(assembler.contentResource()),
                new GetUserAuthorRoute(assembler.userResource()),
                new GetAllDomainsRoute(assembler.domainResource()),
                new GetDomainsHierarchyRoute(assembler.domainResource()),
                new GetAllTagsRoute(assembler.tagResource()),
                new GetPopularDomainRoute(assembler.domainResource()),
                new GetPopularTagRoute(assembler.tagResource()),
                new SimpleHtmlRoute("/user/profile.html", "user/profile.ftl"),
                new GetUserInformationsRoute(assembler.userResource()),
                new PutUserInformationsRoute(assembler.userResource()),
                new SimpleHtmlRoute("/login.html", "login.ftl"),
                new GetContentTypeRoute(assembler.contentResource()),
                new GetContentCriteriasRoute(assembler.contentResource()),
                new GetAdvancedSearchContent(assembler.contentResource()),
                new GetViewContentRoute(assembler.contentResource()),
                new GetEditContentRoute(assembler.contentResource(), assembler.eegResource()),
                new GetUserPreferredDomainsRoute(assembler.userResource()),
                new RemoveUploadedEegRoute(assembler.eegResource()),
                new UploadRoute(assembler.contentResource()),
                new RemoveUploadedFilesRoute()
            ).start();
            System.out.println("Ready, if you dare");
        }catch(Throwable t){
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }

}
