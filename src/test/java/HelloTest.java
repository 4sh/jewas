import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class HelloTest {

    public HelloTest() {

    }

    @Test
    public void testHello() {
        assertThat(true, is(true));
    }
}