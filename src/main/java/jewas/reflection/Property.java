package jewas.reflection;

import java.lang.reflect.Method;

public class Property<B,T> {
	public static final Property<?, ?> NULL = new Property<Object, Void>(null,null,"",Object.class,Void.class);
	
	private final Method getter;
	private final Method setter;
	private final String name;
	private final Class<B> clazz;
	private final Class<T> type;
	
	public Property(Method getter, Method setter, String name, Class<B> clazz, Class<T> type) {
		super();
		this.getter = getter;
		this.setter = setter;
		this.name = name;
		this.clazz = clazz;
		this.type = type;
	}
	
	public boolean hasGetter() {
		return getter != null;
	}

	public boolean hasSetter() {
		return setter != null;
	}

	@SuppressWarnings("unchecked")
	public T get(B o) {
		if (getter != null) {
			try {
				return (T)getter.invoke(o);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public B set(B o, T v) {
		if (setter != null) {
			try {
				return (B)setter.invoke(o, v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return o;
		}
	}

	public String name() {
		return name;
	}
	
	public Class<B> clazz() {
		return clazz;
	}

	public Class<T> type() {
		return type;
	}
}
