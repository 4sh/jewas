package jewas.persistence.sqlparam;


import jewas.persistence.QueryContext;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fcamblor
 */
public class SqlParameter {

    private String paramName;
    private ValuedType paramValuedType;

    private SqlParameter(String paramName, ValuedType type) {
        this.paramName = paramName;
        this.paramValuedType = type;
    }

    public String paramName() {
        return paramName;
    }

    public Object value(){
        return this.paramValuedType.value();
    }

    public String generatePreparedStatementParams() {
        return this.paramValuedType.generatePreparedStatementParams();
    }

    public int fillPreparedStatementParameters(PreparedStatement ps, int index) throws SQLException {
        return this.paramValuedType.fillPreparedStatementParameters(ps, index);
    }

    public static class Builder {
        private List<SqlParameter> parameters = new ArrayList<SqlParameter>();
        private QueryContext context;

        public Builder(QueryContext context){
            this.context = context;
        }

        public QueryContext toContext() {
            this.context.queryParameters(parameters);
            return this.context;
        }

        public Builder date(String paramName, Date paramValue) {
            parameters.add(new SqlParameter(paramName, ValuedTypes.date(paramValue)));
            return this;
        }

        public Builder string(String paramName, String paramValue) {
            parameters.add(new SqlParameter(paramName, ValuedTypes.string(paramValue)));
            return this;
        }

        public Builder integer(String paramName, int paramValue){
            return integer(paramName, Integer.valueOf(paramValue));
        }

        public Builder integer(String paramName, Integer paramValue) {
            parameters.add(new SqlParameter(paramName, ValuedTypes.integer(paramValue)));
            return this;
        }

        public Builder bigint(String paramName, long paramValue){
            return bigint(paramName, Long.valueOf(paramValue));
        }

        public Builder bigint(String paramName, Long paramValue) {
            parameters.add(new SqlParameter(paramName, ValuedTypes.bigint(paramValue)));
            return this;
        }

        public Builder decimal(String paramName, BigDecimal paramValue) {
            parameters.add(new SqlParameter(paramName, ValuedTypes.decimal(paramValue)));
            return this;
        }

        public <T> Builder array(String paramName, T... paramValue) {
            parameters.add(new SqlParameter(paramName, ValuedTypes.<T>array(paramValue)));
            return this;
        }
    }
}
