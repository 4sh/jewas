package jewas.persistence.sqlparam;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fcamblor
 */
public abstract class ValuedType {
    protected Object underlyingValue = null;

    protected ValuedType(Object underlyingValue) {
        this.underlyingValue = underlyingValue;
    }

    public String queryValue() {
        if (underlyingValue == null) {
            return "NULL";
        } else {
            return toNonNullQueryValue();
        }
    }

    protected abstract String toNonNullQueryValue();

    // ValuedType implementations...

    public static class DateValuedType extends ValuedType {
        private static final DateFormat QUERY_VALUE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
        private Date date;

        public DateValuedType(Date date) {
            super(date);
            this.date = date;
        }

        protected String toNonNullQueryValue() {
            return QUERY_VALUE_FORMATTER.format(date);
        }
    }

    public static class StringValuedType extends ValuedType {
        private String str;

        public StringValuedType(String str) {
            super(str);
            this.str = str;
        }

        protected String toNonNullQueryValue() {
            return str;
        }
    }

    public static class SqlValuedType extends ValuedType {
        private String sql;

        public SqlValuedType(String sql) {
            super(sql);
            this.sql = sql;
        }

        protected String toNonNullQueryValue() {
            return sql;
        }
    }

    public static class IntegerValuedType extends ValuedType {
        private Integer integer;

        public IntegerValuedType(Integer integer) {
            super(integer);
            this.integer = integer;
        }

        protected String toNonNullQueryValue() {
            return integer.toString();
        }
    }

    public static class DecimalValuedType extends ValuedType {
        private BigDecimal decimalNumber;

        public DecimalValuedType(BigDecimal decimalNumber) {
            super(decimalNumber);
            this.decimalNumber = decimalNumber;
        }

        protected String toNonNullQueryValue() {
            return decimalNumber.toString();
        }
    }

    public static class ArrayValuedType<T extends ValuedType> extends ValuedType {
        private T[] array;

        public ArrayValuedType(T[] array) {
            super(array);
            this.array = array;
        }

        protected String toNonNullQueryValue() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length - 1; i++) {
                sb.append(array[i].queryValue()).append(",");
            }
            if (array.length != 0) {
                sb.append(array[array.length - 1].queryValue());
            }
            return sb.toString();
        }
    }
}
