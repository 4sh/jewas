package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.EegResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class GetEegSettingsRoute extends AbstractRoute {
    private EegResource eegResource;

    public GetEegSettingsRoute(EegResource _eegResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/eeg/settings/[id]"));
        eegResource = _eegResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                request.respondJson().object(eegResource.getEegSettings(oi.id()));
            }
        };
    }
}
