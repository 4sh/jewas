package jewas.persistence.sqlparam;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fcamblor
 */
public abstract class ValuedType<T> {
    protected T underlyingValue = null;

    protected ValuedType(T underlyingValue) {
        this.underlyingValue = underlyingValue;
    }

    public String queryValue() {
        if (underlyingValue == null) {
            return "NULL";
        } else {
            // FIXME: avoid SQL injections here ...
            return toNonNullQueryValue();
        }
    }

    protected static String quotedString(String str) {
        return String.format("\"%s\"", str.replaceAll("\"", "\\\\\""));
    }

    protected abstract String toNonNullQueryValue();

    // ValuedType implementations...

    public static class DateValuedType extends ValuedType<Date> {
        private static final DateFormat QUERY_VALUE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

        // TODO: pass the resulting date format to the constructor ?
        // Is there a standardized jdbc date format type ???
        public DateValuedType(Date date) {
            super(date);
        }

        protected String toNonNullQueryValue() {
            return quotedString(QUERY_VALUE_FORMATTER.format(underlyingValue));
        }
    }

    public static class StringValuedType extends ValuedType<String> {

        public StringValuedType(String str) {
            super(str);
        }

        protected String toNonNullQueryValue() {
            return quotedString(underlyingValue);
        }
    }

    public static class IntegerValuedType extends ValuedType<Integer> {

        public IntegerValuedType(Integer integer) {
            super(integer);
        }

        protected String toNonNullQueryValue() {
            return underlyingValue.toString();
        }
    }

    public static class DecimalValuedType extends ValuedType<BigDecimal> {

        public DecimalValuedType(BigDecimal decimalNumber) {
            super(decimalNumber);
        }

        protected String toNonNullQueryValue() {
            return underlyingValue.toString();
        }
    }

    public static class ArrayValuedType<V extends ValuedType> extends ValuedType<V[]> {

        public ArrayValuedType(V[] array) {
            super(array);
        }

        protected String toNonNullQueryValue() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < underlyingValue.length - 1; i++) {
                sb.append(underlyingValue[i].queryValue()).append(",");
            }
            if (underlyingValue.length != 0) {
                sb.append(underlyingValue[underlyingValue.length - 1].queryValue());
            }
            return sb.toString();
        }
    }
}
