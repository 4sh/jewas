package jewas.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Properties<T> {
	private final List<Property<T,?>> properties;
	
	public Properties(List<Property<T, ?>> properties) {
		this.properties = properties;
	}
	
	public List<Property<T, ?>> asList() {
		return properties;
	}
	
	@SuppressWarnings("unchecked")
	public Property<T,?> property(String name) {
		for (Property<T,?> p : properties) {
			if (p.name().equals(name)) {
				return p;
			}
		}
		return (Property<T, ?>) Property.NULL;
	}

	@SuppressWarnings("unchecked")
	public static <B> Properties<B> properties(Class<B> clazz) {
		List<Property<B,?>> properties = new ArrayList<Property<B,?>>();
		for (Method m : clazz.getDeclaredMethods()) {
			if (isGetter(m)) {
				Method s = findSetter(clazz, m.getName(), m.getReturnType());
				properties.add(new Property<B,Object>(m, s, m.getName(), 
						clazz, (Class<Object>) m.getReturnType()));
			}
		}
		return new Properties<B>(properties);
	}

	private static boolean isGetter(Method m) {
		return m.getParameterTypes().length == 0
				&& m.getReturnType() != Void.class;
	}
	
	private static boolean isSetter(Method m) {
		return m.getParameterTypes().length == 1
				&& m.getReturnType() == m.getDeclaringClass();
	}
	
	private static <B, T> Method findSetter(Class<B> clazz, String name, Class<T> propertyType) {
		try {
			Method m = clazz.getMethod(name, propertyType);
			if (isSetter(m)) {
				return m;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
