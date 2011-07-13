package jewas.converters;

public class IntegerConverter implements Converter<String, Integer> {
	// TODO: better handling of conversion exception
	@Override
	public Integer to(String from) {
		return Integer.valueOf(from);
	}

	@Override
	public String from(Integer to) {
		return String.valueOf(to);
	}

}
