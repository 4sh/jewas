package jewas.persistence;

import jewas.persistence.exception.CannotGetJDBCConnectionException;
import jewas.persistence.exception.DataAccessException;
import jewas.persistence.util.JDBCUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fcamblor
 */
public class QueryTemplate<T> {

    private RowMapper<T> rowMapper;
    private DataSource datasource;

    public QueryTemplate(DataSource datasource, RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
        this.datasource = datasource;
    }

    private static interface InConnectionCallback<T> {
        public void doInConnection(Connection connection);
    }

    public void execute(final InConnectionCallback action) throws DataAccessException {
        Connection connection = null;
        try {
            connection = datasource.getConnection();
        } catch (SQLException e) {
            throw new CannotGetJDBCConnectionException("Cannot get JDBC Connection", e);
        }

        try {
            action.doInConnection(connection);
        } finally {
            JDBCUtils.closeConnectionIfNecessary(connection);
        }
    }

    public void selectObjectsAndFill(final List<T> objects, final String sql, final QueryContext context) {
        // TODO: check @NonNull of objects with a specialized framework like CheckerFramework or Intellij implem ?

        execute(new InConnectionCallback<T>() {
            @Override
            public void doInConnection(Connection connection) {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement(sql);

                    // TODO fill preparedStatement with QueryContext !
                    // context.setValues(ps);

                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        objects.add(rowMapper.processRow(rs));
                    }

                } catch (SQLException ex) {
                    throw new DataAccessException("Exception while executing prepared statement", ex);
                } finally {
                    JDBCUtils.closeStatementIfNecessary(ps);
                    JDBCUtils.closeConnectionIfNecessary(connection);
                }
            }
        });
    }

    public T selectObject(String sql, QueryContext context) {
        List<T> unaryList = new ArrayList<T>(1);
        selectObjectsAndFill(unaryList, sql, context);

        // TODO: assert unaryList.size()==1

        return unaryList.iterator().next();
    }

}
