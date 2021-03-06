package jewas.http;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class PatternUriMatcherTest {

    @Test
    public void shouldBasicTestsOnPatternUriMatcherBeOk() {
        Parameters params = new PatternUriPathMatcher("/user/[param1]/edit").match("/user/1/edit");
        assertThat(params, is(notNullValue()));
        assertThat(params.val("param1"), is(equalTo("1")));
    }

    @Test
    public void shouldOptionalParametersDontBreakThePatternUriMatcher() {
        Parameters params = new PatternUriPathMatcher("/[param1]/[param2]").match("/val1");
        assertThat(params, is(notNullValue()));
        assertThat(params.val("param1"), is(equalTo("val1")));
        assertThat(params.val("param2"), is(equalTo("")));
    }

    @Test
    public void shouldLastParameterBeRetrievedEvenIfItContainsSlashes() {
        Parameters params = new PatternUriPathMatcher("/path/[param1]/[param2]").match("/path/val1/val2/val3/val4");
        assertThat(params, is(notNullValue()));
        assertThat(params.val("param1"), is(equalTo("val1")));
        assertThat(params.val("param2"), is(equalTo("val2/val3/val4")));
    }

    @Test
    public void shouldNonParameterizedUncompleteRouteNotMatch() {
        Parameters params = new PatternUriPathMatcher("/content/search").match("/content/search.html");
        assertThat(params, is(nullValue()));
    }

    @Test
    public void shouldRootUrlMatches(){
        Parameters params = new PatternUriPathMatcher("/").match("/");
        assertThat(params, is(notNullValue()));
    }

}
