package jewas.http;


import jewas.http.impl.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;

public class RedirectResponse {
	private HttpResponse httpResponse;

	public RedirectResponse(DefaultHttpRequest defaultHttpRequest, HttpResponse response) {
		this.httpResponse = response;
        this.httpResponse.status(HttpStatus.SEE_OTHER).contentType(ContentType.TXT_HTML);
	}

	public void location(String location) {
		httpResponse
                .addHeader(HttpHeaders.Names.LOCATION, location)
                .content("<p>Redirection</p>");
	}
}
