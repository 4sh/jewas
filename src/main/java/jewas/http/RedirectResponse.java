package jewas.http;


import org.jboss.netty.handler.codec.http.HttpHeaders;

public class RedirectResponse {
	private HttpResponse httpResponse;

	public RedirectResponse(HttpResponse response) {
		this.httpResponse = response;
	}

	public void location(String location) {
		httpResponse
                .status(HttpStatus.SEE_OTHER)
                .contentType(new ContentType("text/html"))
                .addHeader(HttpHeaders.Names.LOCATION, location)
                .content("<p>Redirection</p>");
	}
}
