package fr.fsh.bbeeg.security.resources;

import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.pojos.User;

/**
 * User: carmarolli
 */
public class ConnectedUserResource {

    private UserDao userDao;

    public static ConnectedUserResource instance;

    private ConnectedUserResource() {
    }

    public static ConnectedUserResource instance() {
        if (instance == null) {
            instance = new ConnectedUserResource();
        }
        return instance;
    }

    public ConnectedUserResource userDao(UserDao userDao) {
        this.userDao = userDao;
        return this;
    }

    public String userNames() {
        //FIXME: should use cookie to guess the currently connected user if any
        User connectedUser = userDao.getUser(1000L);
        if (connectedUser == null) {
            return "anonymous";
        }
        return connectedUser.surname() + " " + connectedUser.name();
    }
}
