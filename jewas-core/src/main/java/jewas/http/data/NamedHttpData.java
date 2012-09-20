package jewas.http.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fcamblor
 * Class representation of named http data read in body content
 * Particularly while reading basic form submissions
 */
public abstract class NamedHttpData<UNDERLYING_TYPE> implements HttpData, Cloneable {
    protected String name;
    // This type should not always be exposed directly via an accessor
    protected List<UNDERLYING_TYPE> values;

    protected NamedHttpData(String name, UNDERLYING_TYPE value){
        this(name, Arrays.asList(value));
    }

    protected NamedHttpData(String name, List<UNDERLYING_TYPE> values){
        this.name = name;
        this.values = new ArrayList<UNDERLYING_TYPE>(values);
    }

    public String name(){
        return this.name;
    }

    public int count(){
        return values.size();
    }

    /**
     * Immutable
     * Append a new NamedHttpData resulting values to current NamedHttpData, and retrieve a defensive copy
     * of the resulting NamedHttpData
     * @param dataToAppend The NamedHttpData result whith values to append
     * @return A new defensive copy of current NamedHttpData, with given NamedHttpData values appended
     */
    public NamedHttpData<UNDERLYING_TYPE> append(NamedHttpData<UNDERLYING_TYPE> dataToAppend){
        List<UNDERLYING_TYPE> newValues = new ArrayList<UNDERLYING_TYPE>(this.values);
        newValues.addAll(dataToAppend.values);
        return newInstance(this.name(), newValues);
    }

    protected abstract NamedHttpData<UNDERLYING_TYPE> newInstance(String name, List<UNDERLYING_TYPE> values);

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append('{');
        sb.append("name='").append(name).append('\'');
        sb.append("values=").append(values.toString());
        sb.append('}');
        return sb.toString();
    }


}
