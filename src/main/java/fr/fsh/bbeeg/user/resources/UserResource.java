package fr.fsh.bbeeg.user.resources;

import fr.fsh.bbeeg.common.resources.DateResultObject;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.pojos.User;
import org.joda.time.DateMidnight;

import java.util.List;

/**
 * @author driccio
 */
public class UserResource {
    private UserDao userDao;

    public UserResource(UserDao userDao) {
        this.userDao = userDao;
    }

    public static DateResultObject getLastConnectionDate() {
        return new DateResultObject(new DateMidnight());
    }

    public static User getUserInformations() {
        // TODO : should not return a User object but something else instead
        User user = new User();
        user.name("Bob").surname("Sponge").email("pacific_33@sea.gl")
                .lastConnectionDate(new DateMidnight().toDate());
        return user;
    }

    public void fetchDomains(List<Domain> results, Integer number, User user) {
        userDao.fetchDomains(results, number, user);
    }

    public User getUser(Long id) {
        return userDao.getUser(id);
    }
}
