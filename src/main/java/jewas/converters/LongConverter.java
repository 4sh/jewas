package jewas.converters;

/**
 * @author driccio
 */
public class LongConverter implements Converter<String, Long> {
	// TODO: better handling of conversion exception
	@Override
	public Long to(String from) {
		return Long.valueOf(from);
	}

	@Override
	public String from(Long to) {
		return String.valueOf(to);
	}

}
