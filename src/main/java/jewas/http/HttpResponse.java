package jewas.http;

public interface HttpResponse {
	HttpResponse status(HttpStatus status);
	HttpResponse contentType(ContentType contentType);
	HttpResponse content(String content);
    HttpResponse content(byte[] content);
}
