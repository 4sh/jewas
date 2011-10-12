package fr.fsh.bbeeg.security.resources;

import fr.fsh.bbeeg.security.persistence.SecurityDao;
import fr.fsh.bbeeg.security.pojos.Security;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.pojos.User;

/**
 * User: carmarolli
 */
public class ConnectedUserResource {

    private UserDao userDao;
    private SecurityDao securityDao;

    public static ConnectedUserResource instance;

    private ConnectedUserResource() {
    }

    public static ConnectedUserResource instance() {
        if (instance == null) {
            instance = new ConnectedUserResource();
        }
        return instance;
    }

    public ConnectedUserResource userDao(UserDao _userDao, SecurityDao _securityDao) {
        this.userDao = _userDao;
        this.securityDao = _securityDao;
        return this;
    }

    public Security getUserSecurity(String securityToken) {
        return securityDao.getSecurity(securityToken);
    }

    public User getUser(String securityToken) {
        return userDao.getUser(securityToken);
    }

}
