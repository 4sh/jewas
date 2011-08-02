package jewas.http;

import jewas.configuration.JewasConfiguration;
import jewas.util.file.Files;

import java.io.IOException;

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

        try {
            request.respondFile().file(Files.getInputStreamFromPath(staticResourcesPath + path));
        } catch (IOException e) {
            // FIXME with a log message
            System.err.println(e.getMessage());
            // TODO : better runtime exception here ?
            throw new RuntimeException("Error while transmitting file with path : "+path, e);
        }
    }
}
