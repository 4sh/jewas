package jewas.http;


import java.nio.ByteBuffer;

public interface HttpRequest {
	public HttpMethod method();
	public String uri();
	public Headers headers();
	public String path();

    /**
     * @deprecated Should not be used directly
     * Prefer using the RequestHandler.offer() and RequestHandler.onReady() methods
     * to retrieve request parsed content
     */
    @Deprecated
    public ByteBuffer content();
	public Parameters parameters();
	public JsonResponse respondJson();
	public HtmlResponse respondHtml();
    public FileResponse respondFile();
    public RedirectResponse redirect();

	public HttpRequest addContentHandler(ContentHandler h);
	public void respondError(HttpStatus status);
}
