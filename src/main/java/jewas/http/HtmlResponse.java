package jewas.http;


public class HtmlResponse {
	private HttpResponse httpResponse;

	public HtmlResponse(HttpResponse response) {
		this.httpResponse = response;
	}

	public void content(String content) {
		httpResponse
			.status(HttpStatus.OK)
			.contentType(new ContentType("text/html"));
		httpResponse.content(content);
	}

}
