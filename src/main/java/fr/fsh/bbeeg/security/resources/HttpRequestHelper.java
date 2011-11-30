package fr.fsh.bbeeg.security.resources;

import jewas.http.HttpHeaders;
import jewas.http.HttpRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: carmarolli
 * Date: 12/10/11
 * Time: 21:13
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestHelper {

    private static final String COOKIE_PATTERN = "login=([^;]+)";

    /**
     * Returns the security token from the HTTP request cookie
     * @param request the current HTTP request
     * @return the security token. For the moment, the login of the connected user.
     */
    public static String getSecurityToken(HttpRequest request) {
        String cookie = request.headers().getHeaderValue(HttpHeaders.COOKIE);

        if (cookie == null || cookie.isEmpty()) {
            return null;
        } else {
            Pattern pattern = Pattern.compile(COOKIE_PATTERN);
            Matcher m = pattern.matcher(cookie);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    public static String getLogin(String securityToken) {
        if (securityToken != null && !securityToken.isEmpty()) {
            return securityToken;
        }
        return null;
    }

}
