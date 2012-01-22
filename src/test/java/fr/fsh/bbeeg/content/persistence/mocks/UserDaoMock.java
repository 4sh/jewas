package fr.fsh.bbeeg.content.persistence.mocks;

import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.pojos.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carmarolli
 * Date: 20/01/12
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
public class UserDaoMock extends UserDao {

    public UserDaoMock() {
        super(null, null);
    }

     public User getUser(Long id) {
        return null;
    }

    public User getUser(String login) {
        return null;
    }

    public void fetchDomains(List<Domain> results, Integer number, User user) {

    }

    public void fetchAllAuthors(List<User> result, int limit) {

    }

    public void updateUser(User user) {

    }
}
