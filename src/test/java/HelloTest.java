import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;

public class HelloTest{

    public HelloTest(){

    }

    @Test
    public void testHello(){
        assertThat(true, is(true));
    }
}