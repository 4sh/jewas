package jewas.persistence.sqlparam;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author fcamblor
 */
public abstract class ValuedType<T> {
    protected T underlyingValue = null;

    protected ValuedType(T underlyingValue) {
        this.underlyingValue = underlyingValue;
    }

    public T value(){
        return underlyingValue;
    }

    public String generatePreparedStatementParams(){
        return "?";
    }

    public int fillPreparedStatementParameters(PreparedStatement ps, int index) throws SQLException {
        ps.setObject(index, underlyingValue);
        return 1;
    }

    // ValuedType implementations...

    public static class DateValuedType extends ValuedType<Date> {
        public DateValuedType(Date date) {
            super(date);
        }
    }

    public static class StringValuedType extends ValuedType<String> {
        public StringValuedType(String str) {
            super(str);
        }
    }

    public static class IntegerValuedType extends ValuedType<Integer> {
        public IntegerValuedType(Integer integer) {
            super(integer);
        }
    }

    public static class LongValuedType extends ValuedType<Long> {
        public LongValuedType(Long l) {
            super(l);
        }
    }

    public static class DecimalValuedType extends ValuedType<BigDecimal> {
        public DecimalValuedType(BigDecimal decimalNumber) {
            super(decimalNumber);
        }
    }

    public static class ArrayValuedType<V> extends ValuedType<V[]> {
        public ArrayValuedType(V[] array) {
            super(array);
        }

        public String generatePreparedStatementParams(){
            StringBuilder sb = new StringBuilder("(");
            for(int i=0; i<underlyingValue.length; i++){
                if(i!=0){
                    sb.append(",");
                }
                sb.append("?");
            }
            return sb.append(")").toString();
        }

        public int fillPreparedStatementParameters(PreparedStatement ps, int index) throws SQLException {
            int i=0;
            for(V val : underlyingValue){
                ps.setObject(index+i, val);
                i++;
            }
            return underlyingValue.length;
        }
    }
}
