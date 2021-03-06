package jewas.http;


import org.jboss.netty.handler.codec.http.Cookie;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class HttpRequest {
	public abstract HttpMethod method();
	public abstract String uri();
	public abstract String path();

    public abstract void addResponseHeader(String name, String value);
	public abstract Headers headers();

    public abstract void responseContentType(ContentType contentType);

    public abstract Cookie cookie(String name);
    public abstract Collection<Cookie> cookies();
    public abstract void addResponseCookie(Cookie cookie);
    public abstract void addRequestCookie(Cookie sessionCookie);

    public abstract void attribute(String name, Object value);
    public abstract Object attribute(String name);
    public abstract Object removeAttribute(String name);

    /**
     * @deprecated Should not be used directly
     * Prefer using the RequestHandler.offer() and RequestHandler.onReady() methods
     * to retrieve request parsed content
     */
    @Deprecated
    public abstract ByteBuffer content();
	public abstract Parameters parameters();
	public abstract JsonResponse respondJson();
	public abstract HtmlResponse respondHtml();
    public abstract FileResponse respondFile();
    public abstract RedirectResponse redirect();

	public abstract HttpRequest addContentHandler(ContentHandler h);
	public abstract void respondError(HttpStatus status);

    /**
     * @return Context URI with query parameters appended to it
     */
    public String fullUri() {
        StringBuilder fullUri = new StringBuilder(uri());
        Map<String,List<String>> parameters = parameters().asMap();
        if(!parameters.isEmpty()){
            fullUri.append("?");
            for(String paramName : parameters.keySet()){
                for(String paramValue : parameters.get(paramName)){
                    fullUri.append(paramName).append("=").append(paramValue);
                }
            }
        }
        return fullUri.toString();
    }
}
