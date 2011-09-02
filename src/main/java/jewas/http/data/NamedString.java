package jewas.http.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author fcamblor
 */
public class NamedString extends NamedHttpData<String> {

    public NamedString(String name, String value){
        super(name, value);
    }

    public NamedString(String name, List<String> values){
        super(name, values);
    }

    @Override
    protected NamedHttpData<String> newInstance(String name, List<String> values) {
        return new NamedString(name, values);
    }

    @Override
    public boolean isCompleted() {
        // by default, String read will always be completed
        return true;
    }

    public String value(){
        return this.values.get(0);
    }

    public List<String> values(){
        return Collections.unmodifiableList(this.values);
    }
}
