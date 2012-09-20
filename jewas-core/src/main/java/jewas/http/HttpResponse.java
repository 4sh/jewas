package jewas.http;

import java.nio.file.Path;

public interface HttpResponse {
	HttpResponse status(HttpStatus status);
	HttpResponse contentType(ContentType contentType);
	HttpResponse content(String content);
    HttpResponse content(Path path);

    HttpResponse addHeader(String header, Object value);
}
