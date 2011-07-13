package jewas.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import jewas.converters.Converters;
import jewas.lang.Strings;
import jewas.reflection.Properties;
import jewas.reflection.Property;

public class QueryObjects {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T toQueryObject(Parameters parameters,
			Class<T> clazz) {
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
			String v = parameters.val(p.name());
			if (v != null && v != Strings.NULL) {
				// TODO: handle conversion exception
				p.set(o, Converters.sconverter(p.type()).to(v));
			}
		}
		return o;
	}
}
