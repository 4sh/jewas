package jewas.http.impl;

import jewas.http.HttpRequest;
import jewas.http.HttpStatus;
import jewas.http.RequestHandler;
import jewas.util.file.Files;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 16:50
 *
 * FileRequestHandler is a {@link RequestHandler} to use to load static resources.
 *
 */
public class FileRequestHandler implements RequestHandler {
    /**
     * The path of the file to load.
     */
    String path;

    public FileRequestHandler(String path) {
        this.path = path;
    }

    /**
     * Loads the static file from the path into the given request.
     * @param request the {@link HttpRequest}
     */
    @Override
    public void onRequest(HttpRequest request) {
        try {
            request.respondFile().file(Files.getInputStreamFromPath(path));
        } catch (IOException e) {
            request.respondError(HttpStatus.NOT_FOUND);
        }
    }
}
