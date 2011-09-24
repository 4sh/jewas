package jewas.http;

import jewas.collection.TypedArrayList;
import jewas.collection.TypedList;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class QueryObjectsTest {


    public static class A {
        private String a1;
        private Integer a2;
        private TypedList<String> a3 = new TypedArrayList<String>(String.class);
        private String[] a4;

        public A a1(String _a1){
            this.a1 = _a1;
            return this;
        }

        public String a1(){
            return this.a1;
        }

        public A a2(Integer _a2){
            this.a2 = _a2;
            return this;
        }

        public Integer a2(){
            return this.a2;
        }

        public A a3(TypedList<String> _a3){
            this.a3 = _a3;
            return this;
        }

        public TypedList<String> a3(){
            return this.a3;
        }

        public A a4(String[] _a4){
            this.a4 = _a4;
            return this;
        }

        public String[] a4(){
            return this.a4;
        }
    }

    public static class B extends A {
        private String b1;
        public B b1(String _b1){
            this.b1 = _b1;
            return this;
        }

        public String b1(){
            return this.b1;
        }
    }

    @Test
    public void shouldReflectionBeMadeCorrectlyOnObject(){
        Map<String, List<String> > paramsMap = new HashMap<String, List<String> >();
        paramsMap.put("a1", Arrays.asList("a1Value"));
        paramsMap.put("a2", Arrays.asList("1234"));
        paramsMap.put("a3", Arrays.asList("a3Value1", "a3Value2"));
        paramsMap.put("a4", Arrays.asList("a4Value1", "a4Value2"));
        Parameters params = new Parameters(paramsMap);
        A a = QueryObjects.toQueryObject(params, A.class);

        assertThat(a.a1(), is(equalTo("a1Value")));
        assertThat(a.a2(), is(equalTo(1234)));
        assertThat(a.a3().toArray(new String[0]),
                is(equalTo(new TypedArrayList<String>(String.class, Arrays.<String>asList("a3Value1", "a3Value2")).toArray(new String[0]))
        ));
        assertThat(a.a4(), is(equalTo(new String[]{ "a4Value1", "a4Value2" })));
    }


    @Test
    public void shouldReflectionBeMadeCorrectlyOnObjectWithAncestors(){
        Map<String, List<String> > paramsMap = new HashMap<String, List<String> >();
        paramsMap.put("a1", Arrays.asList("a1Value"));
        paramsMap.put("a2", Arrays.asList("1234"));
        paramsMap.put("a3", Arrays.asList("a3Value1", "a3Value2"));
        paramsMap.put("a4", Arrays.asList("a4Value1", "a4Value2"));
        paramsMap.put("b1", Arrays.asList("b1Value"));
        Parameters params = new Parameters(paramsMap);
        B b = QueryObjects.toQueryObject(params, B.class);

        assertThat(b.a1(), is(equalTo("a1Value")));
        assertThat(b.a2(), is(equalTo(1234)));
        assertThat(b.a3().toArray(new String[0]),
                is(equalTo(new TypedArrayList<String>(String.class, Arrays.<String>asList("a3Value1", "a3Value2")).toArray(new String[0]))
        ));
        assertThat(b.a4(), is(equalTo(new String[]{ "a4Value1", "a4Value2" })));
        assertThat(b.b1(), is(equalTo("b1Value")));
    }
}
