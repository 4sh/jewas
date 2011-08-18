package jewas.http;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Headers {
    /**
     * headers is a List<Map.Entry<String, String>> because netty defines it like that.
     * And netty defines it like that because a same header key can be defined several times
     * (like HTTP parameters).
     */
	private final /* @Immutable */ List</* @Immutable */ Map.Entry<String, String>> headers;

	public Headers(List<Entry<String, String>> headers) {
		this.headers = Collections.unmodifiableList(headers);
	}
	
	public List<Map.Entry<String, String>> asList() {
		return headers;
	}

    public String getHeaderValue(String header) {
        if (header == null || "".equals(header)) {
            return null;
        }

        for (Entry<String, String> entry: headers) {
            if (header.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }
}
