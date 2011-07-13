package jewas.http;


import jewas.json.Json;

public class JsonResponse {
	private HttpResponse httpResponse;

	public JsonResponse(HttpResponse response) {
		this.httpResponse = response;
	}

	public void object(Object o) {
		httpResponse
			.status(HttpStatus.OK)
			.contentType(new ContentType("application/json"));
		httpResponse.content(Json.toJsonString(o));	
	}

}
