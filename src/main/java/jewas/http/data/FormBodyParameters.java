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
                if(!(nhd instanceof NamedString)){
                    throw new IllegalStateException(
                            String.format("2 non-string parameters encountered in form body for key %s", nhd.name()));
                }
                NamedString nsToAdd = (NamedString)nhd;

                HttpData existingData = namedData.get(nhd.name());
                if(!(existingData instanceof NamedString)){
                    throw new IllegalStateException(
                            String.format("Trying to provide multiple values to existing non-string parameter " +
                                    "in form body for key %s", nhd.name()));
                }
                NamedString nsExistingData = (NamedString)existingData;

                List<String> newValues = new ArrayList<String>(nsExistingData.values());
                newValues.add(nsToAdd.value());
                namedData.put(nhd.name(), new NamedString(nhd.name(), newValues));
            }
        }
    }

    public NamedHttpData get(String name){
        return namedData.get(name);
    }
}
