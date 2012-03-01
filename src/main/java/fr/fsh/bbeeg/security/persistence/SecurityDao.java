package fr.fsh.bbeeg.security.persistence;

import fr.fsh.bbeeg.security.pojos.Security;
import fr.fsh.bbeeg.security.resources.ConnectionInformation;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecurityDao {

    private QueryTemplate<Security> securityQueryTemplate;

    public SecurityDao(DataSource dataSource) {
        init(dataSource);
    }

    private void init(DataSource dataSource) {

        // Initializing QueryTemplates
        this.securityQueryTemplate =
                new QueryTemplate<Security>(dataSource, new SecurityRowMapper())
                        .addQuery("selectSecurity", "SELECT u.LOGIN, u.NAME, u.SURNAME, r.ROLENAME " +
                                "FROM USER AS u INNER JOIN ROLE AS r ON u.ROLE_REF = r.ID " +
                                "WHERE u.LOGIN = :login AND u.PASSWORD = :password")
                        .addQuery("selectFullSecurity", "SELECT u.LOGIN, u.NAME, u.SURNAME, r.ROLENAME " +
                                "FROM USER AS u INNER JOIN ROLE AS r ON u.ROLE_REF = r.ID " +
                                "WHERE u.LOGIN = :login");

    }

    public Security getSecurity(ConnectionInformation info) {
        return securityQueryTemplate.selectObject("selectSecurity",
                new QueryExecutionContext().buildParams()
                        .string("login", info.login())
                        .string("password", info.password())
                        .toContext());
    }

     public Security getSecurity(String login) {
        return securityQueryTemplate.selectObject("selectFullSecurity",
                new QueryExecutionContext().buildParams()
                        .string("login", login)
                        .toContext());
    }

    private class SecurityRowMapper implements RowMapper<Security> {

        @Override
        public Security processRow(ResultSet resultSet) throws SQLException {

            Security security = new Security();
            security.login(resultSet.getString("LOGIN"))
                    .name(resultSet.getString("NAME"))
                    .surname(resultSet.getString("SURNAME"))
                    .role(resultSet.getString("ROLENAME"));
            return security;
        }
    }
}
