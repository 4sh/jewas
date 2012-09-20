package jewas.routes.security.josso;

import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.Route;
import jewas.http.data.BodyParameters;
import jewas.http.data.HttpData;
import jewas.http.session.Sessions;
import jewas.routes.RedirectRoute;
import org.jboss.netty.channel.ChannelLocal;
import org.josso.agent.http.JOSSOSecurityContext;
import org.josso.agent.http.WebAccessControlUtil;

/**
 * @author fcamblor
 */
public abstract class JOSSOWebAccessControlAbstractRoute implements Route {

    private RedirectRoute jossoRedirectRoute;

    public JOSSOWebAccessControlAbstractRoute(final String jossoAuthentUrl, String gatewayWebservice){
        jossoRedirectRoute = new RedirectRoute(null, jossoAuthentUrl);
    }

    @Override
    public RequestHandler match(HttpRequest request) {
        // If session is already open, current route should not be triggered
        if(sessionOpened(request)){
            return null;
        } else if(tokenProvided(request)){
            // Here, token should be provided by JOSSO after authentication
            // TODO: Call JOSSO webservice to verify token already exists (and retrieve user information)
            // then store it into the session in order to have sessionOpened() returns true
            // If token is invalid, we should redirect on JOSSO
            String token = retrieveToken(request);
            if(tokenIsValid(request, token)){
                return null;
            } else {
                return jossoRedirectRoute.redirectRequestHandler(request);
            }
        } else {
            // This is the first time we enter BB-EEG : we should redirect on JOSSO
            return jossoRedirectRoute.redirectRequestHandler(request);
        }
    }

    protected boolean tokenIsValid(HttpRequest request, String token){
        // TODO: Implement this
        Object jossoResponse = jossoAuthentication(token);
        if(jossoResponse != null){
            Object userInfos = createUserInfos(jossoResponse);
            storeUserInfosInSession(request, userInfos);
            return true;
        } else {
            return false;
        }
    }

    protected void storeUserInfosInSession(HttpRequest request, Object userInfos){
        // TODO: Implement this : store userInfos into the session, and provide generic accessors to it
    }

    protected Object jossoAuthentication(String token){
        // TODO: Implement this : should call josso webservice to authenticate user by token
        // then return a "josso response" (common JOSSO object)
        return null;
    }

    private boolean tokenProvided(HttpRequest request) {
        return retrieveToken(request) != null;
    }

    private String retrieveToken(HttpRequest request) {
        // TODO: Implement this : returns null if token is not provided
        return null;
    }

    private boolean sessionOpened(HttpRequest request) {
        // TODO: Implement this !
        return false;
    }

    /**
     * Subclasses should implement this method to convert jossoResponse into a domain-specific
     * user object which will be stored in the session
     */
    protected abstract Object createUserInfos(Object jossoResponse);

    public static JOSSOSecurityContext getSecurityContext(HttpRequest request){
        return (JOSSOSecurityContext) Sessions.get(request, WebAccessControlUtil.KEY_JOSSO_SECURITY_CONTEXT);

    }
}
