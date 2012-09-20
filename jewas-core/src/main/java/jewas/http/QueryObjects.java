package jewas.http;

import jewas.collection.TypedList;
import jewas.converters.Converters;
import jewas.http.data.FileUpload;
import jewas.http.data.FormBodyParameters;
import jewas.http.data.NamedHttpData;
import jewas.http.data.NamedString;
import jewas.reflection.Properties;
import jewas.reflection.Property;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                if(TypedList.class.isAssignableFrom(p.type())){
                    handleMultiValuedListField(o, p, d);
                } else if(p.type().isArray()){
                    handleMultiValuedArrayField(o, p, d);
                } else {
                    handleSingleValuedField(o, p, d);
                }
			}
		}
		return o;
	}

    private static <T> void handleSingleValuedField(T o, Property p, NamedHttpData d) {
        Object fieldValue = null;
        // TODO: handle conversion exception
        // TODO: improve this ? I don't see how...
        if(d instanceof FileUpload){
            if(!FileUpload.class.equals(p.type())){
                throw new UnsupportedOperationException(
                        String.format("Cannot bind FileUpload http data to %s.%s (target type should be a %s !)",
                                p.type(), p.name(), FileUpload.class.getCanonicalName()));
            }
            fieldValue = (FileUpload)d;
        } else if(d instanceof NamedString){
            fieldValue = Converters.sconverter(p.type()).to(((NamedString)d).value());
        } else {
            throw new IllegalStateException("No conversion strategy found for NamedHttpData : "+d.getClass().getName());
        }

        p.set(o, fieldValue);
    }

    private static <T> void handleMultiValuedListField(T o, Property p, NamedHttpData d) {
        if(!(d instanceof NamedString)){
            throw new IllegalStateException(String.format("Cannot map %s to TypedList %s.%s type !",
                    d.getClass().getCanonicalName(), p.type().getCanonicalName(), p.name()));
        }

        NamedString ns = (NamedString)d;
        TypedList targetList = (TypedList)p.get(o);
        if(targetList == null){
            // TypedList must have been instantiated in order to determine its component type
            throw new IllegalStateException(String.format("You must instantiate TypeList in %s.%s !",
                    p.type().getCanonicalName(), p.name()));
        }
        // Ensuring list is empty
        targetList.clear();

        for(String val : ns.values()){
            targetList.add(Converters.sconverter(targetList.getComponentType()).to(val));
        }
    }

    private static <T> void handleMultiValuedArrayField(T o, Property p, NamedHttpData d) {
        if(!(d instanceof NamedString)){
            throw new IllegalStateException(String.format("Cannot map %s to Arrayed %s.%s type !",
                    d.getClass().getCanonicalName(), p.type().getCanonicalName(), p.name()));
        }

        NamedString ns = (NamedString)d;
        Object array = Array.newInstance(p.type().getComponentType(), ns.values().size());

        int i=0;
        for(String val : ns.values()){
            Array.set(array, i, Converters.sconverter(p.type().getComponentType()).to(val));
            i++;
        }

        p.set(o, array);
    }
}
