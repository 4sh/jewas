package jewas.security.util;

import org.josso.agent.SingleSignOnEntry;
import org.josso.gateway.SSONameValuePair;
import org.josso.gateway.identity.service.BaseUserImpl;

/**
 * @author fcamblor
 */
public class SSOEntries {

    public static String getPrincipalLogin(SingleSignOnEntry entry) {
        return entry.principal.getName();
    }

    public static String findPrincipalProperty(SingleSignOnEntry entry, String propName) {
        for (SSONameValuePair ssoNameValuePair : ((BaseUserImpl) entry.principal).getProperties()) {
            if (propName.equals(ssoNameValuePair.getName())) {
                return ssoNameValuePair.getValue();
            }
        }
        return null;
    }
}
