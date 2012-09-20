package jewas.http;

import jewas.http.data.BodyParameters;
import jewas.http.data.FormBodyParameters;
import jewas.http.data.HttpData;

import java.util.List;

public interface RequestHandler {
	public abstract void onRequest(HttpRequest request);
    public abstract void offer(HttpRequest request, HttpData data);
    public abstract void onReady(HttpRequest request, BodyParameters bodyParameters);
}
