package jewas.lang;

public class Strings {
	public static final String NULL = new String();

    public static boolean isNullOrEmptyString(String s) {
        return s == null || s.isEmpty();
    }
}
