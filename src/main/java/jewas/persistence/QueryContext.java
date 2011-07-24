package jewas.persistence;

import jewas.persistence.sqlparam.SqlParameter;

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

    private static final Pattern QUERY_PARAM_DETECTOR = Pattern.compile("\\$\\{(\\w+)\\}");
    private List<SqlParameter> queryParameters;

    public List<SqlParameter> queryParameters() {
        return queryParameters;
    }

    public QueryContext queryParameters(List<SqlParameter> p) {
        this.queryParameters = p;
        return this;
    }

    /**
     * Will compute sql query by replacing named parameters by their value
     *
     * @param sql the sql query to compute
     * @return the computed sql query
     */
    public String compute(String sql) {
        String previousSql = null;
        String computedSql = sql;

        // Computing sql query again and again.. until no query parameter is relying into it
        while (!computedSql.equals(previousSql)) {
            previousSql = computedSql;
            StringBuffer sb = new StringBuffer();
            Matcher queryParamMatcher = QUERY_PARAM_DETECTOR.matcher(previousSql);
            while (queryParamMatcher.find()) {
                String paramName = queryParamMatcher.group(1);
                String value = parameterValue(paramName);
                // to fix IllegalArgumentEception: Illegal group reference
                // (see : http://veerasundar.com/blog/2010/01/java-lang-illegalargumentexception-illegal-group-reference-in-string-replaceall/
                value = Matcher.quoteReplacement(value);
                queryParamMatcher.appendReplacement(sb, value == null ? "" : value);
            }
            queryParamMatcher.appendTail(sb);

            computedSql = sb.toString();
        }

        return computedSql;
    }

    protected String parameterValue(String paramName) {
        if (queryParameters() == null) {
            return null;
        }

        for (SqlParameter p : queryParameters()) {
            if (p.paramName().equals(paramName)) {
                return p.value();
            }
        }

        return null;
    }
}
