package fr.fsh.bbeeg.security.resources;

import fr.fsh.bbeeg.security.persistence.SecurityDao;
import fr.fsh.bbeeg.security.pojos.Security;

/**
 * @author driccio
 */
public class SecurityResource {

    private final SecurityDao securityDao;

    public SecurityResource(SecurityDao _securityDao) {
        securityDao = _securityDao;
    }

    /**
     * Connection method.
     * @param info connection information entered byt he user in the login form.
     * @return a Security object if the user is authenticated, <code>null</code> instead.
     */
    public Security connectUser(ConnectionInformation info) {
        // TODO : update the last connection date if this user is successfully connected
        return securityDao.getSecurity(info);
    }

    public void disconnectUser() {
        // Nothing to do for the moment, invalidate cache in the future.
    }
}
