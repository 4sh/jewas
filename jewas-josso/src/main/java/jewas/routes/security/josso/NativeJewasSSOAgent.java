package jewas.routes.security.josso;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.josso.agent.SSOAgentRequest;
import org.josso.gateway.identity.SSOUser;
import org.josso.gateway.identity.exceptions.SSOIdentityException;

import java.security.Principal;

/**
 * @author fcamblor
 */
public class NativeJewasSSOAgent extends JewasSSOAgent {
    private static final Log log = LogFactory.getLog(NativeJewasSSOAgent.class);

    public NativeJewasSSOAgent() {
        super();
    }

    /**
     * Resolves an authentication request directly against the gateway.
     *
     * @param request containing the SSO Session id.
     * @return null if no principal can be authenticated using the received SSO Session Id
     */
    protected Principal authenticate(SSOAgentRequest request) {
        String ssoSessionId = request.getSessionId();
        try {
            if (ssoSessionId == null) {
                log.debug("Session authentication failed : " + ssoSessionId);
                return null;
            }

            SSOUser ssoUser = getSSOIdentityManager().findUserInSession(request.getRequester(), ssoSessionId);

            log.debug("Session authentication succeeded : " + ssoSessionId);
            return ssoUser;
        } catch (SSOIdentityException e) {
            // Ignore this ... (user does not exist for this session)
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            return null;
        } catch (Exception e) {
            log.error("Session authentication failed : " + ssoSessionId, e);
            throw new RuntimeException("Fatal error authenticating session : " + e);
        }
    }

    @Override
    protected void log(String message) {
        if (debug > 0) {
            log.debug(message);
        }
    }

    @Override
    protected void log(String message, Throwable throwable) {
        if (debug > 0) {
            log.debug(message, throwable);
        }
    }
}
