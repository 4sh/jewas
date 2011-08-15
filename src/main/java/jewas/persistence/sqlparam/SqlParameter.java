package jewas.persistence.sqlparam;


import java.math.BigDecimal;
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

    public String value() {
        return paramValuedType.queryValue();
    }


    public static class Builder {
        private List<SqlParameter> parameters = new ArrayList<SqlParameter>();

        public List<SqlParameter> parameters() {
            return parameters;
        }

        public List<SqlParameter> andThatsAll() {
            return parameters();
        } // Syntaxic sugar

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
