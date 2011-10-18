package fr.fsh.bbeeg.user.persistence;

import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.pojos.User;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author driccio
 */
public class UserDao {
    private DomainDao domainDao;
    private QueryTemplate<User> userQueryTemplate;

    public UserDao(DataSource dataSource, DomainDao domainDao) {
        this.domainDao = domainDao;

        this.userQueryTemplate =
                new QueryTemplate<User>(dataSource, new UserRowMapper())
                        .addQuery("selectById", "select * from User where id = :id")
                        .addQuery("selectByLogin", "select * from User where login = :login")
                        .addQuery("selectLimitedAuthors", "select * from (select distinct(u.*) from User u " +
                                "inner join Content c on c.author_ref = u.id and c.status = :status order by u.name asc)" +
                                "where ROWNUM <= :limit")
                        .addQuery("updateInfos", "update User set name = :lastName, surname = :firstName, email = :email where login = :login");
    }

    public User getUser(Long id) {
        User user = userQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", id)
                        .toContext()
        );

        return user;
    }

    public User getUser(String login) {
        User user = userQueryTemplate.selectObject("selectByLogin",
                new QueryExecutionContext().buildParams()
                        .string("login", login)
                        .toContext()
        );

        return user;
    }

    public void fetchDomains(List<Domain> results, Integer number, User user) {
        // TODO: use a good way to get these domains
        domainDao.fetchPopularDomains(results, number);
    }

    public void fetchAllAuthors(List<User> result, int limit) {
       userQueryTemplate.select(result, "selectLimitedAuthors",
           new QueryExecutionContext()
                   .buildParams()
                   .integer("status", ContentStatus.VALIDATED.ordinal())
                   .integer("limit", limit)
                   .toContext());
   }

    public void updateUser(User user) {
        userQueryTemplate.update("updateInfos", new QueryExecutionContext()
            .buildParams()
            .string("login", user.login())
            .string("firstName", user.firstName())
            .string("lastName", user.lastName())
            .string("email", user.email())
            .toContext());
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User processRow(ResultSet rs) throws SQLException {
            // TODO: Get the last connection date after having added the DB missing column
            return new User()
                    .id(rs.getLong("id"))
                    .login(rs.getString("login"))
                    .lastName(rs.getString("name"))
                    .firstName(rs.getString("surname"))
                    .email(rs.getString("email"));
        }
    }
}
