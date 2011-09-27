package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.content.pojos.EegSettings;
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
public class CreateEegSettingsRoute extends AbstractRoute {
    private EegResource eegResource;

    public CreateEegSettingsRoute(EegResource _eegResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/eeg/settings/[id]"));
        eegResource = _eegResource;
    }

    public static class TextQueryObject {
        private String text;

        public TextQueryObject text(String _text){
            this.text = _text;
            return this;
        }

        public String text(){
            return this.text;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId qo = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                EegSettings eegSetting = toContentObject(bodyParameters, EegSettings.class);

                eegResource.updateEegSettings(qo.id(), eegSetting);

                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}