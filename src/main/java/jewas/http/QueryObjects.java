package jewas.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jewas.converters.Converters;
import jewas.http.data.*;
import jewas.lang.Strings;
import jewas.reflection.Properties;
import jewas.reflection.Property;

public class QueryObjects {

    public static <T> T toQueryObject(Parameters parameters, Class<T> clazz){
        FormBodyParameters bodyParams = paramsToNamedStrings(parameters);
        return toContentObject(bodyParams, clazz);
    }

    public static FormBodyParameters paramsToNamedStrings(Parameters params){
        List<NamedHttpData> httpData = new ArrayList<NamedHttpData>();
        for (Map.Entry<String, List<String> > entry : params.asMap().entrySet()) {
            httpData.add(new NamedString(entry.getKey(), entry.getValue()));
        }
        return new FormBodyParameters(httpData);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T toContentObject(FormBodyParameters bodyParameters, Class<T> clazz) {
		Properties<T> props = Properties.properties(clazz);
		
		T o;
		try {
			Constructor<T> c = clazz.getDeclaredConstructor();
			c.setAccessible(true);
			o = c.newInstance(new Object[0]);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		for (Property p : props.asList()) {
            NamedHttpData d = bodyParameters.get(p.name());
			if (d != null) {
				// TODO: handle conversion exception
                // TODO: improve this ? I don't see how...
                if(d instanceof FileUpload){
                    if(!FileUpload.class.equals(p.type())){
                        throw new UnsupportedOperationException(
                                String.format("Cannot bind FileUpload http data to type <%s> on property <%s> (target type should be a FileUpload !)",
                                        p.type(), p.name()));
                    }
                    p.set(o, (FileUpload)d);
                } else if(d instanceof NamedString){
                    p.set(o, Converters.sconverter(p.type()).to(((NamedString)d).value()));
                } else {
                    throw new IllegalStateException("No conversion strategy found for NamedHttpData : "+d.getClass().getName());
                }
			}
		}
		return o;
	}
}
