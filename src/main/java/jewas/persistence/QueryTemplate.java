package jewas.persistence;

import jewas.persistence.exception.CannotGetJDBCConnectionException;
import jewas.persistence.exception.DataAccessException;
import jewas.persistence.util.JDBCUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    static interface InConnectionCallback {
        public void doInConnection(Connection connection);
    }

    private static abstract class OpenPreparedStatementInConnection implements InConnectionCallback{

        private String sqlQuery;
        private QueryContext context;

        public OpenPreparedStatementInConnection(String sqlQuery, QueryContext context){
            this.sqlQuery = sqlQuery;
            this.context = context;
        }

        @Override
        public void doInConnection(Connection connection) {
            PreparedStatement ps = null;
            try {
                String psQuery = context.toPreparedStatementSql(sqlQuery);

                ps = createPreparedStatement(connection, psQuery);
                context.fillPreparedStatementParameters(ps, sqlQuery);

                doWithPreparedStatement(ps);

            } catch (SQLException ex) {
                throw new DataAccessException("Exception while executing prepared statement", ex);
            } finally {
                JDBCUtils.closeStatementIfNecessary(ps);
                JDBCUtils.closeConnectionIfNecessary(connection);
            }
        }

        protected PreparedStatement createPreparedStatement(Connection c, String sql) throws SQLException{
            return c.prepareStatement(sql);
        }

        public abstract void doWithPreparedStatement(PreparedStatement ps) throws SQLException;
    }

    private static class ExecuteUpdateQueryWithPreparedStatement extends OpenPreparedStatementInConnection {

        int rowsUpdated = 0;

        public ExecuteUpdateQueryWithPreparedStatement(String sqlQuery, QueryContext context){
            super(sqlQuery, context);
        }

        @Override
        public void doWithPreparedStatement(PreparedStatement ps) throws SQLException {
            rowsUpdated = ps.executeUpdate();
        }

        public int rowsUpdated(){
            return this.rowsUpdated;
        }
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

    public int executeUpdate(String sql, QueryContext context) throws DataAccessException {
        ExecuteUpdateQueryWithPreparedStatement callback = new ExecuteUpdateQueryWithPreparedStatement(sql, context);
        execute(callback);
        return callback.rowsUpdated();
    }

    public void selectObjectsAndFill(final List<T> objects, final String sql, final QueryContext context) {
        // TODO: check @NonNull of objects with a specialized framework like CheckerFramework or Intellij implem ?

        execute(new OpenPreparedStatementInConnection(sql, context) {
            @Override
            public void doWithPreparedStatement(PreparedStatement ps) throws SQLException {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    objects.add(rowMapper.processRow(rs));
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

    public void insert(final String sql, final QueryContext context){
        insert(sql, context, null);
    }

    /**
     * Insert a row in the database
     * @param sql The insert sql query
     * @param context Current Querycontext
     * @param generatedKeyColumns List of column names which will be auto-filled by the database
     * (auto increment / sequence fields).
     * IMPORTANT NOTE : The key columns ordering must be the same as the calling of auto increment / sequence
     * execution ordering !
     * For example, if we call "insert into azerty (id1, id2) values (seq.nextval, seq.nextval) the generatedKeyColumns
     * must be filled with ["id1", "id2"]
     * @return A Map having every generated column values. It is in the form [column name => generated value ]
     */
    public Map<String,String> insert(final String sql, final QueryContext context, final String... generatedKeyColumns){
        final Map<String, String> genKeys = new HashMap<String, String>();

        execute(new OpenPreparedStatementInConnection(sql, context) {
            @Override
            protected PreparedStatement createPreparedStatement(Connection c, String sql) throws SQLException {
                if(generatedKeyColumns==null || generatedKeyColumns.length==0){
                    return super.createPreparedStatement(c,sql);
                }else{
                    return c.prepareStatement(sql,generatedKeyColumns);
                }
            }

            @Override
            public void doWithPreparedStatement(PreparedStatement ps) throws SQLException {
                ps.executeUpdate();

                // Retrieving generated data
                ResultSet genKeysResultSet = ps.getGeneratedKeys();
                if(genKeysResultSet.next()){
                    for(int i=0; i<generatedKeyColumns.length; i++){
                        genKeys.put(generatedKeyColumns[i], genKeysResultSet.getString(i+1));
                    }
                }
            }
        });

        return genKeys;
    }

    public int update(final String sql, final QueryContext context){
        return executeUpdate(sql, context);
    }

    public int delete(final String sql, final QueryContext context){
        return executeUpdate(sql, context);
    }
}
