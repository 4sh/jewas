package jewas.lang;

public class Objects {
	public static final Object NULL = new Object();
	
	public static <T> T NULL(Class<T> clazz) {
		// TODO: check if the class defines a NULL constant, 
		// or if there exists a plural form of the class defining such a constant
		return null;
	}

	public static boolean isNull(Object o) {
		return o == null || o == NULL(o.getClass());
	}
}
