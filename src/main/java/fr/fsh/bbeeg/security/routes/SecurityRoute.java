package fr.fsh.bbeeg.security.routes;

import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.Route;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;

public class SecurityRoute implements Route {


    @Override
    public RequestHandler match(HttpRequest request) {
        //TODO Replace by cookie check

        String securityToken = HttpRequestHelper.getSecurityToken(request);
        if (securityToken != null) {
            return null;
        } else {
            return new AbstractRequestHandler() {
                @Override
                public void onRequest(HttpRequest request) {
                    request.respondHtml().content(Templates.process("login.ftl", null));
                }
            };
        }
    }
}
