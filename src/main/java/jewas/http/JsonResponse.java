package jewas.http;


import jewas.json.Json;

import java.lang.reflect.Type;

public class JsonResponse {
    private HttpResponse httpResponse;

    public JsonResponse(HttpResponse response) {
        this.httpResponse = response;
    }

    public void object(Object o) {
        object(o, null);
    }

    public void object(Object o, Type parameterizedType) {
        httpResponse
                .status(HttpStatus.OK)
                .contentType(new ContentType("application/json"));
        httpResponse.content(Json.instance().toJsonString(o, parameterizedType));
    }

}
