package jewas.http.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fcamblor
 */
public class FormBodyParameters implements BodyParameters {
    protected final Map<String, NamedHttpData > namedData = new HashMap<String, NamedHttpData >();

    public FormBodyParameters(List<NamedHttpData> httpData){
        for(NamedHttpData nhd : httpData){
            namedData.put(nhd.name(), nhd);
        }
    }

    public NamedHttpData get(String name){
        return namedData.get(name);
    }
}
