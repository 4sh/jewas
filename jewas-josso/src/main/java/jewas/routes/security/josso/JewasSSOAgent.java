package jewas.routes.security.josso;


import jewas.configuration.JewasConfiguration;
import jewas.http.ContentType;
import jewas.http.HttpRequest;
import jewas.http.session.Sessions;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.josso.agent.AbstractSSOAgent;
import org.josso.agent.Constants;
import org.josso.agent.SSOAgentRequest;
import org.josso.agent.SSOPartnerAppConfig;
import org.josso.agent.http.SecurityContextExporterFilter;
import org.josso.auth.util.CipherUtil;
import org.josso.gateway.identity.SSORole;
import org.josso.gateway.identity.SSOUser;
import org.josso.gateway.identity.exceptions.SSOIdentityException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;

/**
 * @author fcamblor
 */
public abstract class JewasSSOAgent extends AbstractSSOAgent {
    private static final String DEFAULT_JOSSO_AUTHENTICATION_URI = "/josso_authentication/";
    private String _jossoAuthenticationUri = DEFAULT_JOSSO_AUTHENTICATION_URI;

    public JewasSSOAgent() {
        super();
    }

    @Override
    protected void sendCustomAuthentication(SSOAgentRequest ssoAgentRequest) throws IOException {
        HttpRequest hreq = ((HttpSSOAgentRequest) ssoAgentRequest).getRequest();
        prepareNonCacheResponse(hreq);

        SSOPartnerAppConfig cfg = extractPartnerAppConfig(hreq);

        String splash_resource = null;
           /* If this is an authentication request, our splash resource will be one of the following (in the given order):
            * 1. submitted josso_splash_resource parameter
            * 2. default splash resource, defined in josso-agent-config
            * TODO : Referer values should be handled by agent when processing LOGIN_REQUESTS (josso_login) 3. value from referrer header
            *
            * If this is not authentication request, splash resource will be request URI
            */
        if (hreq.uri().endsWith(this.getJossoAuthenticationUri())) {
            //try josso_splash_resource defined as hidden field
            splash_resource = hreq.parameters().val(Constants.JOSSO_SPLASH_RESOURCE_PARAMETER);

            if (splash_resource == null || "".equals(splash_resource)) {
                if (cfg != null) {
                    splash_resource = cfg.getSplashResource();
                }
                   /* TODO :Verify this! Agents should store referer values as SAVED_REQUESTS when
                   processing a login or automatic request
           		if(splash_resource == null || "".equals(splash_resource)){
           			//fall back to referer
           			splash_resource = hreq.getHeader("referer");
           		}
                    */
            }
        } else {

            if (debug > 0)
                log("sendCustomAuthentication executed but URL does not match AUTHENTICATION URI");

            // TODO : Verify this! We should never get here ..

            String[] uriArray = new String[1];
            splash_resource = hreq.fullUri();
        }

        if (debug > 0)
            log("Storing Splash resource '" + splash_resource + "'");

        setAttribute(hreq, Constants.JOSSO_SPLASH_RESOURCE_PARAMETER, splash_resource);

        StringBuilder sb = new StringBuilder();

        // TODO : Use a template instead ?
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n" +
                "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n"
                + "<body onload=\"document.forms[0].submit()\">\n" +
                "<noscript>\n" + "<p>\n" + "<strong>Note:</strong> Since your browser does not support JavaScript,\n" +
                "you must press the Continue button once to proceed.\n" + "</p>\n" + "</noscript>\n" +
                "<form action=\"").append(getGatewayLoginUrl()).
                append("\" method=\"post\" name=\"usernamePasswordLoginForm\" enctype=\"application/x-www-form-urlencoded\">\n"
                        + "        <div>");

        //copy all submitted parameters into hidden fields
        Enumeration paramNames = Collections.enumeration(hreq.parameters().names());
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String paramValue = hreq.parameters().val(paramName);
            if (!Constants.JOSSO_SPLASH_RESOURCE_PARAMETER.equals(paramName)) {
                sb.append("\n            <input type=\"hidden\" value=\"").append(paramValue).append("\" name=\"").append(paramName).append("\" />");
            }
        }

        //        sb.append("\n            <input type=\"hidden\" name=\"josso_back_to\"value=\"").append(buildBackToURL(hreq, getJossoSecurityCheckUri())).append("\"/>\n").
        sb.append("\n            <noscript><input type=\"submit\" value=\"Continue\"/></noscript>\n" +
                "        </div>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>");


        hreq.responseContentType(ContentType.TXT_HTML);
        hreq.respondHtml().content(sb.toString());

        if (debug >= 1) {
            log("Sending an automatic post form : \n" + sb.toString());
        }
    }

    protected void propagateSecurityContext(SSOAgentRequest request, Principal principal) {
        HttpSSOAgentRequest ssoAgentRequest = (HttpSSOAgentRequest) request;
        SSOPartnerAppConfig partnerAppConfig = extractPartnerAppConfig(ssoAgentRequest.getRequest());

        if (partnerAppConfig.getSecurityContextPropagationConfig() == null) {
            // No security propagation configuration found, ignore this.
            return;
        }

        String binding = partnerAppConfig.getSecurityContextPropagationConfig().getBinding();
        String userPlaceHolder = partnerAppConfig.getSecurityContextPropagationConfig().getUserPlaceHolder();
        String rolesPlaceHolder = partnerAppConfig.getSecurityContextPropagationConfig().getRolesPlaceHolder();
        String propertiesPlaceholder = partnerAppConfig.getSecurityContextPropagationConfig().getPropertiesPlaceHolder();
        String user = principal.getName();
        String nodeId = request.getNodeId();

        if (binding != null && userPlaceHolder != null && rolesPlaceHolder != null) {
            SSORole[] roleSets;

            try {
                if (nodeId != null && !"".equals(nodeId)) {
                    NodeServices svcs = servicesByNode.get(nodeId);
                    if (svcs != null) {
                        roleSets = svcs.getIm().findRolesBySSOSessionId(request.getRequester(), ssoAgentRequest.getSessionId());
                    } else {
                        roleSets = im.findRolesBySSOSessionId(request.getRequester(), ssoAgentRequest.getSessionId());
                    }
                } else {
                    roleSets = im.findRolesBySSOSessionId(request.getRequester(), ssoAgentRequest.getSessionId());
                }
            } catch (SSOIdentityException e) {
                if (debug > 0)
                    log("Error fetching roles for SSO Session [" + ssoAgentRequest.getSessionId() + "]" +
                            " on attempting to propagate security context, aborting");

                return;
            }

            HttpRequest hreq = ssoAgentRequest.getRequest();

            if (binding.equalsIgnoreCase("HTTP_HEADERS")) {

                HashMap headers = new HashMap();
                List users = new ArrayList();
                users.add(user);
                headers.put(userPlaceHolder, users);

                if (debug > 0)
                    log("Propagated user [" + user + "] onto HTTP Header [" + userPlaceHolder + "]");

                List roles = new ArrayList();
                for (int i = 0; i < roleSets.length; i++) {
                    SSORole roleSet = roleSets[i];

                    roles.add(roleSet.getName());

                    if (debug > 0)
                        log("Propagated role [" + roleSet.getName() + "] onto HTTP_HEADERS based security context");
                }
                headers.put(rolesPlaceHolder, roles);

                hreq.attribute(SecurityContextExporterFilter.SECURITY_CONTEXT_BINDING,
                        SecurityContextExporterFilter.HTTP_HEADERS_BINDING);

                hreq.attribute(SecurityContextExporterFilter.SECURITY_CONTEXT_CONTENT, headers);

            } else if (binding.equalsIgnoreCase("HREQ_ATTRS")) {

                HashMap attrs = new HashMap();
                attrs.put(userPlaceHolder, user);

                for (int i = 0; i < roleSets.length; i++) {
                    SSORole roleSet = roleSets[i];
                    attrs.put(rolesPlaceHolder + "_" + i, roleSet.getName());

                    if (debug > 0)
                        log("Propagated role [" + roleSet.getName() + "] onto HREQ_ATTRS based security context");
                }

                SSOUser usr = (SSOUser) principal;
                if (usr.getProperties() != null) {
                    Properties props = new Properties();
                    for (int i = 0; i < usr.getProperties().length; i++) {
                        attrs.put(propertiesPlaceholder + "_" + usr.getProperties()[i].getName(),
                                usr.getProperties()[i].getValue());

                        if (debug > 0)
                            log("Propagated role [" + usr.getProperties()[i].getName() + "=" +
                                    usr.getProperties()[i].getValue() + "] onto HREQ_ATTRS based security context");
                    }
                }

                hreq.attribute(SecurityContextExporterFilter.SECURITY_CONTEXT_CONTENT, attrs);

                hreq.attribute(SecurityContextExporterFilter.SECURITY_CONTEXT_BINDING,
                        SecurityContextExporterFilter.HTTP_REQ_ATTRS_BINDING);

            }
        }
    }

    /**
     * Sets non cache headers in HttpResponse
     *
     * @param request
     */
    public void prepareNonCacheResponse(HttpRequest request) {
        request.addResponseHeader("Cache-Control", "no-cache");
        request.addResponseHeader("Pragma", "no-cache");
        request.addResponseHeader("Expires", "0");
    }

    /**
     * Sets attribute as a cookie (if stateOnClient enabled)
     * or in the http session.
     * Value is base64 encoded.
     *
     * @param hreq  http request
     * @param name  attribute name
     * @param value attribute value
     */
    public void setAttribute(HttpRequest hreq,
                             String name,
                             String value) {

        if (isStateOnClient()) {

            Set<String> removed = (Set<String>) hreq.attribute("org.josso.attrs.removed");
            if (removed == null)
                removed = new HashSet<String>();

            if (removed.contains(name))
                removed.remove(name);

            log("Storing attribute " + name + "=" + value + " client side");

            String cookieValue = null;
            try {
                // TODO: upgrade to commons-codec 1.4 and use URL-safe mode?
                cookieValue = CipherUtil.encodeBase64(value.getBytes());
                cookieValue = URLEncoder.encode(cookieValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log("Base64 encoding failed : " + value, e);
                cookieValue = value;
            }

            // Store value as session cookie
            Cookie cookie = new DefaultCookie(name, cookieValue);
            cookie.setPath(extractContextPath(hreq).equals("") ? "/" : extractContextPath(hreq));
            cookie.setMaxAge(-1);
            cookie.setSecure(secureRequest(hreq));

            hreq.addResponseCookie(cookie);

            // Local copy
            hreq.attribute(name, value);

        } else {

            log("Storing attribute " + name + "=" + value + " server side");
            // Use HTTP Session ( TODO : Use LocalSession instead ? )
            Sessions.get(hreq).set(name, value);
        }

    }

    protected SSOPartnerAppConfig extractPartnerAppConfig(HttpRequest hreq) {
        return this.getPartnerAppConfig(extractServerName(hreq), extractContextPath(hreq));
    }

    protected boolean secureRequest(HttpRequest request) {
        return false;
    }

    /**
     * Retrieves attribute value from the cookie (if stateOnClient enabled)
     * or from the http session.
     *
     * @param hreq http request
     * @param name attribute name
     * @return attribute value
     */
    public String getAttribute(HttpRequest hreq, String name) {
        if (isStateOnClient()) {

            Set<String> removed = (Set<String>) hreq.attribute("org.josso.attrs.removed");
            if (removed == null)
                removed = new HashSet<String>();

            if (removed.contains(name))
                return null;

            // If a local value is present, use it.
            String vlocal = (String) hreq.attribute(name);
            if (vlocal != null && !"".equals(vlocal))
                return vlocal;

            // Use a cookie value, if present
            Collection<Cookie> cookies = hreq.cookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name)) {
                        String cookieValue = cookie.getValue();
                        String value = null;
                        try {
                            // TODO: upgrade to commons-codec 1.4 and use URL-safe mode?
                            cookieValue = URLDecoder.decode(cookieValue, "UTF-8");
                            value = new String(CipherUtil.decodeBase64(cookieValue));
                        } catch (UnsupportedEncodingException e) {
                            log("Base64 decoding failed : " + cookieValue, e);
                            value = cookieValue;
                        }
                        if (value == null || value.equals("-") || value.equals(""))
                            return null;
                        return value;
                    }
                }
            }

            return null;
        } else {
            // Use HTTP Session ( TODO : Use LocalSession instead ? )
            return (String) Sessions.get(hreq).get(name);
        }
    }


    public Cookie newJossoCookie(String path, String value, boolean secure) {
        // Some browsers don't like cookies without paths. This is useful for partner applications configured in the root context
        if (path == null || "".equals(path))
            path = JewasConfiguration.contextPath();

        Cookie ssoCookie = new DefaultCookie(org.josso.gateway.Constants.JOSSO_SINGLE_SIGN_ON_COOKIE, value);
        ssoCookie.setMaxAge(-1);
        ssoCookie.setPath(path);
        ssoCookie.setSecure(secure);

        // TODO : Check domain ?
        //ssoCookie.setDomain(cfg.getSessionTokenScope());


        return ssoCookie;
    }

    protected String extractServerName(HttpRequest request) {
        // In jewas, server name should be extracted with a configuration constant
        return JewasConfiguration.serverName();
    }

    protected String extractContextPath(HttpRequest request) {
        return JewasConfiguration.contextPath();
    }

    /**
     * By default we do not require to authenticate all requests.
     */
    @Override
    protected boolean isAuthenticationAlwaysRequired() {
        return false;
    }

    public String getJossoAuthenticationUri() {
        return _jossoAuthenticationUri;
    }
}
