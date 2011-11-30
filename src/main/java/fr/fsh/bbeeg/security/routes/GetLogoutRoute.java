package fr.fsh.bbeeg.security.routes;

import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
import jewas.http.*;
import jewas.http.HttpHeaders;
import jewas.http.HttpRequest;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;
import org.jboss.netty.handler.codec.http.*;

import java.util.Date;

public class GetLogoutRoute extends AbstractRoute {

    public GetLogoutRoute() {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/logout"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {

            @Override
            public void onRequest(HttpRequest request) {
                String securityToken = HttpRequestHelper.getSecurityToken(request);
                String login = HttpRequestHelper.getLogin(securityToken);
                Date date = new Date();
                date.setTime(date.getTime() - 1000 * 10);
                HtmlResponse htmlResponse = request.respondHtml();
                htmlResponse.addHeader(HttpHeaders.SET_COOKIE, "login=" + login + "; domain=bbeeg.4sh.fr ; expires=" + date.toGMTString());
                htmlResponse.content(Templates.process("login.ftl", null));
            }
        };
    }
}
