package jewas.http;

public class ContentType {
	private String name;

	public ContentType(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
