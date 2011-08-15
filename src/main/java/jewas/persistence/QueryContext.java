package jewas.persistence;

import jewas.persistence.exception.DataAccessException;
import jewas.persistence.sqlparam.SqlParameter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class will contain every Query informations like pagination parameters,
 * query parameters, ordering etc..
 *
 * @author fcamblor
 */
public class QueryContext {

    private static final String QUERY_PARAM_DETECTOR_STRING = ":(\\w+)";
    private static final Pattern QUERY_PARAM_DETECTOR = Pattern.compile(QUERY_PARAM_DETECTOR_STRING);
    private List<SqlParameter> queryParameters;

    public List<SqlParameter> queryParameters() {
        return queryParameters;
    }

    public QueryContext queryParameters(List<SqlParameter> p) {
        this.queryParameters = p;
        return this;
    }

    public String toPreparedStatementSql(String sqlQuery){
        final StringBuffer sb = new StringBuffer();

        computeQuery(sqlQuery, new QueryReader() {
            @Override
            public void queryParamDetected(String paramName, Matcher queryParamMatcher) {
                queryParamMatcher.appendReplacement(sb, findSqlParameter(paramName).generatePreparedStatementParams());
            }
            @Override
            public void end(Matcher queryParamMatcher){
                queryParamMatcher.appendTail(sb);
            }
        });

        return sb.toString();
    }

    public void fillPreparedStatementParameters(final PreparedStatement ps, final String sqlQuery){
        final int[] index = new int[1];
        index[0] = 1;
        computeQuery(sqlQuery, new QueryReader() {
            @Override
            public void queryParamDetected(String paramName, Matcher queryParamMatcher) {
                try {
                    int numberOfFilledParams = findSqlParameter(paramName).fillPreparedStatementParameters(ps, index[0]);
                    index[0] += numberOfFilledParams;
                } catch(SQLException e){
                    throw new DataAccessException("Error while binding query parameter <"+paramName+"> in query : "+sqlQuery, e);
                }
            }
        });
    }

    private static abstract class QueryReader {
        public void start(Matcher queryParamMatcher){ }
        public abstract void queryParamDetected(String paramName, Matcher queryParamMatcher);
        public void end(Matcher queryParamMatcher){ }
    }

    protected void computeQuery(String query, QueryReader callback){
        Matcher queryParamMatcher = QUERY_PARAM_DETECTOR.matcher(query);
        callback.start(queryParamMatcher);
        while (queryParamMatcher.find()) {
            String paramName = queryParamMatcher.group(1);
            callback.queryParamDetected(paramName, queryParamMatcher);
        }
        callback.end(queryParamMatcher);
    }

    protected SqlParameter findSqlParameter(String paramName) {
        if (queryParameters() == null) {
            return null;
        }

        for (SqlParameter p : queryParameters()) {
            if (p.paramName().equals(paramName)) {
                return p;
            }
        }

        return null;
    }
}
