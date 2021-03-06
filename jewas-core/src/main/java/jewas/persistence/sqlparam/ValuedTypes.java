package jewas.persistence.sqlparam;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fcamblor
 */
public class ValuedTypes {
    public static ValuedType.DateValuedType date(Date paramValue) {
        return new ValuedType.DateValuedType(paramValue);
    }

    public static ValuedType.StringValuedType string(String paramValue) {
        return new ValuedType.StringValuedType(paramValue);
    }

    public static ValuedType.IntegerValuedType integer(Integer paramValue) {
        return new ValuedType.IntegerValuedType(paramValue);
    }

    public static ValuedType.LongValuedType bigint(Long paramValue) {
        return new ValuedType.LongValuedType(paramValue);
    }

    public static ValuedType.DecimalValuedType decimal(BigDecimal paramValue) {
        return new ValuedType.DecimalValuedType(paramValue);
    }

    public static <T> ValuedType.ArrayValuedType<T> array(T... paramValue) {
        return new ValuedType.ArrayValuedType<T>(paramValue);
    }
}
