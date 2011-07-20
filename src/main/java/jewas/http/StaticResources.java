package jewas.http;

import jewas.configuration.JewasConfiguration;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class StaticResources {
    /**
     * Load the static file freom the path into the given request.
     * @param request the {@link HttpRequest}
     * @param path the static file path
     */
    public static void loadStaticFile(HttpRequest request, String path) {
        String staticResourcesPath = JewasConfiguration.getStaticResourcesPath();

        // TODO: Use our own File API
        request.respondFile().file(new File(StaticResources.class.getClassLoader().getResource(staticResourcesPath + path).getPath()));
    }
}
