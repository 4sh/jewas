package jewas.http;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Headers {
	private final /* @Immutable */ List</* @Immutable */ Map.Entry<String, String>> headers;

	public Headers(List<Entry<String, String>> headers) {
		this.headers = Collections.unmodifiableList(headers);
	}
	
	public List<Map.Entry<String, String>> asList() {
		return headers;
	}
}
