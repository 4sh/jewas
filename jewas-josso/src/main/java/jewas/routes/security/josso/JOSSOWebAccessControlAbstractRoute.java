package jewas.routes.security.josso;

import jewas.configuration.JossoJewasConfiguration;
import jewas.http.HttpRequest;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.Route;
import jewas.http.session.Sessions;
import jewas.routes.RedirectRoute;
import org.jboss.netty.handler.codec.http.Cookie;
import org.josso.agent.*;
import org.josso.agent.http.JOSSOSecurityContext;
import org.josso.agent.http.WebAccessControlUtil;

import java.io.Serializable;

/**
 * @author fcamblor
 */
public abstract class JOSSOWebAccessControlAbstractRoute implements Route {

    private static final String KEY_JOSSO_SAVED_REQUEST_URI = "jewas.josso.savedRequest";

    private PatternUriPathMatcher logoutUriPathMatcher;
    private boolean invalidateSessionOnLogout;
    private JewasSSOAgent agent;

    public JOSSOWebAccessControlAbstractRoute(String logoutPath) {
        this(logoutPath, true);
    }

    public JOSSOWebAccessControlAbstractRoute(String logoutPath, boolean invalidateSessionOnLogout) {
        this.logoutUriPathMatcher = new PatternUriPathMatcher(logoutPath);
        this.invalidateSessionOnLogout = invalidateSessionOnLogout;

        Lookup lookup = Lookup.getInstance();
        lookup.init("josso-agent-config.xml"); // For spring compatibility ...

        // We need at least an abstract SSO Agent
        try {
            agent = (JewasSSOAgent) lookup.lookupSSOAgent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO: To remove !
        agent.setDebug(1);
        agent.start();
    }

    @Override
    public RequestHandler match(HttpRequest request) {
        // If session is already open, current route should not be triggered...
        if (JossoUser.sessionOpened(request)) {
            // ... except if we are on logout url !
            if (logoutUriPathMatcher.match(request.path()) != null) {
                return handleLogout(request);
            } else {
                // Current match should not match : we should proceed to the next route !
                return null;
            }
        } else {
            // Here, token should be provided by JOSSO after authentication
            if (tokenProvided(request)) {
                // TODO: Call JOSSO webservice to verify token already exists (and retrieve user information)
                // then store it into the session in order to have sessionOpened() returns true
                // If token is invalid, we should redirect on JOSSO
                String token = retrieveToken(request);
                if (askForAuthentication(request, token)) {
                    // No problem here, route's job is finished and we should redirect on
                    // saved url
                    String originalUri = savedRequestUri(request);
                    savedRequestUri(request, null); // Removing saved request uri
                    return new RedirectRoute.RedirectRequestHandler(originalUri);
                } else {
                    return redirectToJosso(request);
                }
            } else {
                // This is the first time we enter BB-EEG : we should redirect on JOSSO
                return redirectToJosso(request);
            }
        }
    }

    protected RequestHandler handleLogout(HttpRequest request) {
        String jossoRedirectUrl = createJossoUrl(request, this.agent.getGatewayLogoutUrl());

        // Clearing previous COOKIE ...
        Cookie ssoCookie = agent.newJossoCookie(agent.extractContextPath(request), "-", agent.secureRequest(request));
        request.addResponseCookie(ssoCookie);

        // Removing user infos
        removeUserInfosInSession(request);

        agent.prepareNonCacheResponse(request);

        return new RedirectRoute.RedirectRequestHandler(jossoRedirectUrl);
    }

    private RequestHandler redirectToJosso(HttpRequest request) {
        String jossoRedirectUrl = createJossoUrl(request, this.agent.getGatewayLoginUrl());

        // Before redirecting, we should store current url in the session
        savedRequestUri(request, request.fullUri());

        return new RedirectRoute.RedirectRequestHandler(jossoRedirectUrl);
    }

    private String createJossoUrl(HttpRequest request, String jossoBaseUrl) {
        return new StringBuilder(jossoBaseUrl)
                .append(jossoBaseUrl.contains("?") ? "&" : "?")
                .append(org.josso.gateway.signon.Constants.PARAM_JOSSO_BACK_TO)
                .append("=")
                .append(JossoJewasConfiguration.jossoBackUrl())
                .append("&")
                .append(org.josso.gateway.signon.Constants.PARAM_JOSSO_PARTNERAPP_ID)
                .append("=")
                .append(this.agent.extractPartnerAppConfig(request).getId())
                .toString();
    }

    protected boolean askForAuthentication(HttpRequest request, String token) {
        SingleSignOnEntry jossoResponse = jossoAuthentication(request, token);
        if (jossoResponse != null) {
            Serializable userInfos = createUserInfos(jossoResponse);
            if (userInfos == null) {
                return false;
            }

            storeUserInfosInSession(request, userInfos);
            return true;
        } else {
            return false;
        }
    }

    protected void removeUserInfosInSession(HttpRequest request) {
        JossoUser.remove(request);
        if (invalidateSessionOnLogout) {
            Sessions.invalidate(Sessions.getId(request));
        }
    }

    protected void storeUserInfosInSession(HttpRequest request, Serializable userInfos) {
        JossoUser.set(request, userInfos);
    }

    protected SingleSignOnEntry jossoAuthentication(HttpRequest request, String token) {
        // Take the node from the request first and store it if found.
        String nodeId = request.parameters().val("josso_node");
        if (nodeId != null) {
            agent.setAttribute(request, "JOSSO_NODE", nodeId);
        } else {
            nodeId = agent.getAttribute(request, "JOSSO_NODE");
            if (!"".equals(nodeId)) {
                nodeId = null;
            }
        }

        SSOPartnerAppConfig cfg = agent.extractPartnerAppConfig(request);
        String cfgId = cfg.getId();
        LocalSession session = new LocalSessionImpl(request);

        HttpSSOAgentRequest ssoRequest = new HttpSSOAgentRequest(cfgId, SSOAgentRequest.ACTION_RELAY, "-", session, token, nodeId);
        ssoRequest.setRequest(request);
        return agent.processRequest(ssoRequest);
    }

    private boolean tokenProvided(HttpRequest request) {
        return !request.parameters().nullVal(Constants.JOSSO_ASSERTION_ID_PARAMETER);
    }

    private String retrieveToken(HttpRequest request) {
        return request.parameters().val(Constants.JOSSO_ASSERTION_ID_PARAMETER);
    }

    public static void savedRequestUri(HttpRequest request, String requestUri) {
        if (requestUri == null) {
            Sessions.get(request).remove(KEY_JOSSO_SAVED_REQUEST_URI);
        } else {
            Sessions.get(request).set(KEY_JOSSO_SAVED_REQUEST_URI, requestUri);
        }
    }

    public static String savedRequestUri(HttpRequest request) {
        return (String) Sessions.get(request).get(KEY_JOSSO_SAVED_REQUEST_URI);
    }

    public static JOSSOSecurityContext getSecurityContext(HttpRequest request) {
        return (JOSSOSecurityContext) Sessions.get(request).get(WebAccessControlUtil.KEY_JOSSO_SECURITY_CONTEXT);

    }

    /**
     * Subclasses should implement this method to convert jossoResponse into a domain-specific
     * user object which will be stored in the session
     */
    protected abstract Serializable createUserInfos(SingleSignOnEntry jossoResponse);
}
