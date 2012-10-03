package jewas.http;

import org.jboss.netty.handler.codec.http.Cookie;

import java.nio.file.Path;

public interface HttpResponse {
    HttpResponse status(HttpStatus status);

    HttpResponse contentType(ContentType contentType);

    HttpResponse content(String content);

    HttpResponse content(Path path);

    HttpResponse addHeader(String header, Object value);

    HttpResponse addCookie(Cookie cookie);

    Cookie cookie(String name);
}
