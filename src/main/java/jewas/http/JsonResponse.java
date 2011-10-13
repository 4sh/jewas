package jewas.http;


import jewas.http.impl.DefaultHttpRequest;
import jewas.json.Json;

import java.lang.reflect.Type;

public class JsonResponse {
    private HttpResponse httpResponse;

    public JsonResponse(DefaultHttpRequest defaultHttpRequest, HttpResponse response) {
        this.httpResponse = response;
        this.httpResponse.status(HttpStatus.OK).contentType(ContentType.APP_JSON);
    }

    public void object(Object o) {
        object(o, null);
    }

    public void object(Object o, Type parameterizedType) {
        httpResponse.content(Json.instance().toJsonString(o, parameterizedType));
    }

    public void addHeader(String header, Object value) {
        this.httpResponse.addHeader(header, value);
    }

}
