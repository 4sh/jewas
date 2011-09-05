package jewas.http.impl;

import jewas.http.HttpRequest;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.data.FormBodyParameters;
import jewas.http.data.HttpData;

import java.util.List;

/**
 * @author fcamblor
 */
public abstract class AbstractRequestHandlerDelegate implements RequestHandler {
    @Override
    public void onRequest(HttpRequest request) {
        for(RequestHandler delegate : findNonNullDelegatesFor(request)){
            delegate.onRequest(request);
        }
    }

    @Override
    public void offer(HttpRequest request, HttpData data) {
        for(RequestHandler delegate : findNonNullDelegatesFor(request)){
            delegate.offer(request, data);
        }
    }

    @Override
    public void onReady(HttpRequest request, BodyParameters bodyParameters) {
        for(RequestHandler delegate : findNonNullDelegatesFor(request)){
            delegate.onReady(request, bodyParameters);
        }
    }

    protected List<RequestHandler> findNonNullDelegatesFor(HttpRequest request){
        List<RequestHandler> delegates = findDelegatesFor(request);
        if(delegates == null || delegates.isEmpty()){
            throw new IllegalStateException(String.format("No request delegate found for request uri %s !", request.uri()));
        }
        return delegates;
    }

    public abstract List<RequestHandler> findDelegatesFor(HttpRequest request);
}
