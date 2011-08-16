package jewas.persistence;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * @author fcamblor
 */
public class Query {

    private String rootSql;

    public Query(String rootSql){
        this.rootSql = rootSql;
    }

    public String toString(){
        return "Query with root sql : "+rootSql;
    }

    private static interface PreparedStatementFactory {
        public PreparedStatement createPreparedStatement(Connection connection, String sql) throws SQLException;
    }

    public PreparedStatement preparedStatementForContext(QueryExecutionContext context,
                                                         Connection connection) throws SQLException{

        return prepareStatement(context, connection, new PreparedStatementFactory() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection, String sql) throws SQLException {
                return connection.prepareStatement(sql);
            }
        });
    }

    public PreparedStatement preparedStatementForContext(QueryExecutionContext context,
                                                         Connection connection,
                                                         final String[] columnsForGeneration) throws SQLException {

        return prepareStatement(context, connection, new PreparedStatementFactory() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection, String sql) throws SQLException {
                return connection.prepareStatement(sql, columnsForGeneration);
            }
        });
    }

    protected PreparedStatement prepareStatement(QueryExecutionContext context,
                                                 Connection connection,
                                                 PreparedStatementFactory psFactory) throws SQLException {

        // 3 steps here for the query :
        // - rootSql is the query defined by developper. Something like this :
        // SELECT foo, bar FROM blah WHERE foo = :foo
        // - psQuery is the translated form of rootSql in the PreparedStatement fashion. Something like this :
        // SELECT foo, bar FROM blah WHERE foo = ?
        // These 2 steps could be pre-processed.
        // - Filling the prepared statement with the contextual parameters (located in QueryExecutionContext)
        // which are runtime params.

        String psQuery = context.toPreparedStatementSql(this.rootSql);

        // TODO :
        // Manage a PreparedStatement cache here with pre-processed queries
        // Difficulty will be concerning :
        // - Conditional portions of the query (not yet implemented)
        // - ArrayValueType which will generate as much "?" in the query as the number of the array's size at runtime
        //
        // The cache could be updated either at the beginning (determining carthesian product of every
        // conditionnal possibility + some amount of array sizes) _or_ lazily when a prepareStatement() call is
        // made

        PreparedStatement ps = psFactory.createPreparedStatement(connection, psQuery);
        context.fillPreparedStatementParameters(ps, this.rootSql);

        return ps;
    }
}
