package jewas.json;

import com.google.gson.Gson;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class JSonTest {
    public static class MyObject<T> {
        public List<T> foo;
        public List<String> bar;
        public String baz;
    }

    public static class SimpleClass {
        public String a;

        public SimpleClass(String _a) {
            this.a = _a;
        }
    }

    @Ignore("Until Issue 350 in GSon is fixed (see http://code.google.com/p/google-gson/issues/detail?id=350)")
    @Test
    public void shouldFooSerializationBeOk() {
        MyObject o = new MyObject<SimpleClass>();
        o.baz = "baz"; // works
        o.bar = new ArrayList<String>();
        o.bar.add("val1");
        o.bar.add("val2"); // works
        o.foo = new ArrayList<SimpleClass>();
        o.foo.add(new SimpleClass("val1"));
        o.foo.add(new SimpleClass("val2")); // doesn't work
        assertThat(new Gson().toJson(o), is(equalTo("{\"foo\":[{\"a\":\"val1\"},{\"a\":\"val2\"}],\"bar\":[\"val1\",\"val2\"],\"baz\":\"baz\"}")));
    }
}
