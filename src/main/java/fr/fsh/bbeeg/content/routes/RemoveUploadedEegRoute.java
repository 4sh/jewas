package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.content.resources.EegResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class RemoveUploadedEegRoute extends AbstractRoute {
    private EegResource eegResource;

    public RemoveUploadedEegRoute(EegResource eegResource){
        super(HttpMethodMatcher.DELETE, new PatternUriPathMatcher("/upload/eeg/[id]"));
        this.eegResource = eegResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

               // eegResource.cleanTmp(oi.id());

                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
