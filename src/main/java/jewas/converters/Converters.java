package jewas.converters;

import jewas.lang.Objects;
import jewas.lang.Strings;

public class Converters {
	@SuppressWarnings("unchecked")
	public static <T> Converter<String, T> sconverter(final Class<T> clazz) {
		if (clazz == String.class) {
			return (Converter<String, T>) new StringConverter();
		}
		if (clazz == Integer.class) {
			return (Converter<String, T>) new IntegerConverter();
		}
		return new Converter<String, T>() {
			@Override
			public T to(String from) {
				return Objects.NULL(clazz);
			}

			@Override
			public String from(T to) {
				return Strings.NULL;
			}
		};
	}
}
