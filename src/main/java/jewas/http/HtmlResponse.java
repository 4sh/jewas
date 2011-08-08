package jewas.http;


import jewas.http.impl.DefaultHttpRequest;

public class HtmlResponse {
	private HttpResponse httpResponse;

	public HtmlResponse(DefaultHttpRequest defaultHttpRequest, HttpResponse response) {
		this.httpResponse = response;
        this.httpResponse.status(HttpStatus.OK).contentType(ContentType.TXT_HTML);
	}

	public void content(String content) {
		httpResponse.content(content);
	}

}
