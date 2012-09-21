package jewas.routes.security.josso;

import jewas.configuration.JewasConfiguration;
import jewas.configuration.JossoJewasConfiguration;
import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.Route;
import jewas.http.session.Sessions;
import jewas.routes.RedirectRoute;
import org.josso.agent.Constants;
import org.josso.agent.SingleSignOnEntry;
import org.josso.agent.http.JOSSOSecurityContext;
import org.josso.agent.http.WebAccessControlUtil;

import java.io.Serializable;

/**
 * @author fcamblor
 */
public abstract class JOSSOWebAccessControlAbstractRoute implements Route {

    private static final String KEY_USER_INFOS = "jewas.user.infos";
    private static final String KEY_JOSSO_SAVED_REQUEST_URI = "jewas.josso.savedRequest";

    private String jossoAuthentUrl;

    public JOSSOWebAccessControlAbstractRoute(final String jossoAuthentUrl, String gatewayWebservice){
        this.jossoAuthentUrl = jossoAuthentUrl;
    }

    @Override
    public RequestHandler match(HttpRequest request) {
        // If session is already open, current route should not be triggered
        if(sessionOpened(request)){
            return null;
        } else {
            // Here, token should be provided by JOSSO after authentication
            if(tokenProvided(request)){
                // TODO: Call JOSSO webservice to verify token already exists (and retrieve user information)
                // then store it into the session in order to have sessionOpened() returns true
                // If token is invalid, we should redirect on JOSSO
                String token = retrieveToken(request);
                if(askForAuthentication(request, token)){
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

    private RequestHandler redirectToJosso(HttpRequest request) {
        StringBuilder jossoRedirectUrl = new StringBuilder(jossoAuthentUrl)
                .append(jossoAuthentUrl.contains("?")?"&":"?")
                .append(org.josso.gateway.signon.Constants.PARAM_JOSSO_BACK_TO)
                .append("=")
                .append(JossoJewasConfiguration.jossoBackUrl());

        // Before redirecting, we should store current url in the session
        savedRequestUri(request, request.fullUri());

        return new RedirectRoute.RedirectRequestHandler(jossoRedirectUrl.toString());
    }

    protected boolean askForAuthentication(HttpRequest request, String token){
        SingleSignOnEntry jossoResponse = jossoAuthentication(token);
        if(jossoResponse != null){
            Serializable userInfos = createUserInfos(jossoResponse);
            storeUserInfosInSession(request, userInfos);
            return true;
        } else {
            return false;
        }
    }

    protected void storeUserInfosInSession(HttpRequest request, Serializable userInfos){
        Sessions.set(request, KEY_USER_INFOS, userInfos);
    }

    protected SingleSignOnEntry jossoAuthentication(String token){
        return null;
    }

    private boolean tokenProvided(HttpRequest request) {
        return !request.parameters().nullVal(Constants.JOSSO_ASSERTION_ID_PARAMETER);
    }

    private String retrieveToken(HttpRequest request) {
        return request.parameters().val(Constants.JOSSO_ASSERTION_ID_PARAMETER);
    }

    private static boolean sessionOpened(HttpRequest request) {
        return userInfos(request) != null;
    }

    public static void savedRequestUri(HttpRequest request, String requestUri){
        if(requestUri == null){
            Sessions.remove(request, KEY_JOSSO_SAVED_REQUEST_URI);
        } else {
            Sessions.set(request, KEY_JOSSO_SAVED_REQUEST_URI, requestUri);
        }
    }

    public static String savedRequestUri(HttpRequest request) {
        return (String) Sessions.get(request, KEY_JOSSO_SAVED_REQUEST_URI);
    }

    public static Serializable userInfos(HttpRequest request){
        return Sessions.get(request, KEY_USER_INFOS);
    }

    /**
     * Subclasses should implement this method to convert jossoResponse into a domain-specific
     * user object which will be stored in the session
     */
    protected abstract Serializable createUserInfos(SingleSignOnEntry jossoResponse);

    public static JOSSOSecurityContext getSecurityContext(HttpRequest request){
        return (JOSSOSecurityContext) Sessions.get(request, WebAccessControlUtil.KEY_JOSSO_SECURITY_CONTEXT);

    }
}
