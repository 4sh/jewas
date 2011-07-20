package jewas.http.impl;

import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.StaticResources;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 16:50
 *
 * StaticResourceRequestHandler is a {@link RequestHandler} to use to load static resources.
 *
 */
public class StaticResourceRequestHandler implements RequestHandler {
    /**
     * The path of the file to load.
     */
    String path;

    public StaticResourceRequestHandler(String path) {
        this.path = path;
    }

    @Override
    public void onRequest(HttpRequest request) {
        StaticResources.loadStaticFile(request, path);
    }
}
