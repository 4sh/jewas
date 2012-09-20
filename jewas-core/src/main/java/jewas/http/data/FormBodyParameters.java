package jewas.http.data;

import java.util.ArrayList;
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
            if(!namedData.containsKey(nhd.name())){
                namedData.put(nhd.name(), nhd);
            } else {
                HttpData existingData = namedData.get(nhd.name());
                if(!(existingData.getClass().equals(nhd.getClass()))){
                    throw new IllegalStateException(
                            String.format("Trying to provide multiple values to differently typed parameters " +
                                    "in form body for key %s", nhd.name()));
                }
                // Previous test ensures existingData is instanceof NamedHttpData since nhd is instanceof NamedHttpData
                NamedHttpData nsExistingData = (NamedHttpData)existingData;

                namedData.put(nhd.name(), nsExistingData.append(nhd));
            }
        }
    }

    public NamedHttpData get(String name){
        return namedData.get(name);
    }
}
