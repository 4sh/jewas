package jewas.persistence;

import jewas.persistence.sqlparam.ValuedType;
import jewas.persistence.sqlparam.ValuedTypes;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class ValuedTypesTest {

    @Test
    public void shouldStringAndDateValuedTypesEscapeQuotes() throws Throwable {
        assertThat(ValuedTypes.string("Hello \"world\" !").queryValue(), is(equalTo("\"Hello \\\"world\\\" !\"")));
        assertThat(ValuedTypes.date(new SimpleDateFormat("dd/MM/yyyy").parse("29/06/1983")).queryValue(), is(equalTo("\"1983-06-29\"")));
    }

    @Test
    public void shouldSqlAndIntegerAndDecimalAndArrayValuedTypesNotEscapeQuotes() throws Throwable {
        assertThat(ValuedTypes.integer(Integer.valueOf(10)).queryValue(), is(equalTo("10")));
        assertThat(ValuedTypes.decimal(new BigDecimal("10.12345")).queryValue(), is(equalTo("10.12345")));
        assertThat(ValuedTypes.sql("blah").queryValue(), is(equalTo("blah")));
        assertThat(ValuedTypes.<ValuedType.StringValuedType>array(ValuedTypes.string("blah"), ValuedTypes.string("Hello \"world\" !")).queryValue(),
                is(equalTo("\"blah\",\"Hello \\\"world\\\" !\"")));
    }
}
