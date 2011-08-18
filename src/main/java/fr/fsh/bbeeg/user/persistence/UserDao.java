package fr.fsh.bbeeg.user.persistence;

import fr.fsh.bbeeg.user.pojos.User;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author driccio
 */
public class UserDao {
    private QueryTemplate<User> userQueryTemplate;

    public UserDao(DataSource dataSource) {
        this.userQueryTemplate =
                new QueryTemplate<User>(dataSource, new UserRowMapper())
                        .addQuery("selectById", "select * from User where id = :id");
    }

    public User getUser(Long authorId) {
        User user = userQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", authorId)
                        .toContext()
        );

        return user;
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
