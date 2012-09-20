package jewas.converters;


public interface Converter<F,T> {
	public T to(F from);
	public F from(T to);
}
