package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.EegResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author carmarolli
 */
public class GetEegSettingsRoute extends AbstractRoute {
    /**
     * Resource used to access eeg settings file on disk.
     */
    private EegResource eegResource;

    /**
     * Default constructor.
     * @param _eegResource resource used to read eeg settings.
     */
    public GetEegSettingsRoute(EegResource _eegResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/visio/eeg/settings/[id]"));
        eegResource = _eegResource;
    }

    /**
     * {@inheritDoc}
     */
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

