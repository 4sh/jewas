package jewas.http.impl;

import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.data.FormBodyParameters;
import jewas.http.data.HttpData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fcamblor
 * Base implementation for most of your request handlers
 */
public class AbstractRequestHandler implements RequestHandler {
    Map<String, String> headers = null;

    /**
     * Callback called just after uri parameters have been parsed
     * Should generally be overloaded on GET routes which will rely on request
     * uri parameters
     * @param request Current request
     */
    @Override
    public void onRequest(HttpRequest request) {
    }

    /**
     * Callback called everytime the body content parser is able to provide
     * a new content data
     * @param request Current request
     * @param data The content data which has just been parsed
     */
    @Override
    public void offer(HttpRequest request, HttpData data) {
    }

    /**
     * Callback called after EVERY body content data has been parsed
     * Should generally be overloaded on POST routes which will rely on
     * POST content data
     * @param request The current request
     * @param bodyParameters Every parsed body content data
     */
    @Override
    public void onReady(HttpRequest request, BodyParameters bodyParameters) {
    }

    public RequestHandler withHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
        return this;
    }
}
