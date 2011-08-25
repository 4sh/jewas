package fr.fsh.bbeeg.user.persistence;

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
                        .addQuery("selectById", "select * from User where id = :id");
    }

    public User getUser(Long id) {
        User user = userQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", id)
                        .toContext()
        );

        return user;
    }

    public void fetchDomains(List<Domain> results, Integer number, User user) {
        // TODO: use a good way to get these domains
        domainDao.fetchPopularDomains(results, number);
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User processRow(ResultSet rs) throws SQLException {
            return new User()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .surname(rs.getString("surname"))
                    .email(rs.getString("email"));
        }
    }

}
