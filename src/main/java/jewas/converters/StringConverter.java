package jewas.converters;

public class StringConverter implements Converter<String, String> {
	// TODO: better handling of conversion exception

	@Override
	public String to(String from) {
		return from;
	}

	@Override
	public String from(String to) {
		return to;
	}
	
}
