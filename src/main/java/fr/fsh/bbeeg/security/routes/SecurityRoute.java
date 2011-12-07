package fr.fsh.bbeeg.security.routes;

import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.Route;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityRoute implements Route {

    /**
     * Class logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(SecurityRoute.class);

    @Override
    public RequestHandler match(HttpRequest request) {
        //TODO Replace by cookie check

        String securityToken = HttpRequestHelper.getSecurityToken(request);
        if (securityToken != null) {
            logger.debug("Security token found: " + securityToken);
            return null;
        } else {
            logger.debug("Security token not found, redirect");
            return new AbstractRequestHandler() {
                @Override
                public void onRequest(HttpRequest request) {
                    request.respondHtml().content(Templates.process("login.ftl", null));
                }
            };
        }
    }
}
