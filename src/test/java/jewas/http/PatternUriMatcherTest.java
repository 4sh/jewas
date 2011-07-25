package jewas.http;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class PatternUriMatcherTest {

    @Test
    public void shouldLastParameterRetrieve() {
        Parameters params = new PatternUriPathMatcher("/path/[param1]/[param2]").match("/path/val1/val2/val3");
        assertThat(params, is(notNullValue()));
        assertThat(params.val("param1"), is(equalTo("val1")));
        assertThat(params.val("param2"), is(equalTo("val2/val3")));
    }
}
