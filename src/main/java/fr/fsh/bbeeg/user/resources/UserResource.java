package fr.fsh.bbeeg.user.resources;

import fr.fsh.bbeeg.common.resources.DateResultObject;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
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

    public User getUserInformations(String securityToken) {
        // TODO : Add lastConnection date to User table in DB and remove this mock
        User user =  userDao.getUser(securityToken);
        user.lastConnectionDate(new DateMidnight().toDate());
        return user;
    }

    public void fetchDomains(List<Domain> results, Integer number, User user) {
        userDao.fetchDomains(results, number, user);
    }

    public void fetchAuthors(List<User> authors, LimitedOrderedQueryObject loqo) {

            if ("all".equals(loqo.ordering())) {
                userDao.fetchAllAuthors(authors, 25);
            } else {
                userDao.fetchAllAuthors(authors, loqo.number());
            }

        }

    public User getUser(Long id) {
        return userDao.getUser(id);
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }
}
