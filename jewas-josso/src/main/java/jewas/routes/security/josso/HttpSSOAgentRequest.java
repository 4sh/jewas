package jewas.routes.security.josso;

import jewas.http.HttpRequest;
import org.josso.agent.LocalSession;
import org.josso.agent.SSOAgentRequestImpl;

/**
 * @author fcamblor
 */
public class HttpSSOAgentRequest extends SSOAgentRequestImpl {
    private HttpRequest request;

    public HttpSSOAgentRequest(String requester,
                               int action,
                               String sessionId,
                               LocalSession session,
                               String assertionId,
                               String nodeId) {
        super(requester, action, sessionId, session, assertionId, nodeId);
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpRequest getRequest() {
        return request;
    }
}
