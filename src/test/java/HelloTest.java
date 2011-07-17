
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HelloTest{

    public HelloTest(){

    }

    @Test
    public void testHello(){
        assertThat(true, is(true));
    }
}