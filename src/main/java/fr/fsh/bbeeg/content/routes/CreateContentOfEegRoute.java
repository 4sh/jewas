package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.resources.FileQueryObject;
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

import java.io.File;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;

/**
 * @author driccio
 */
public class CreateContentOfEegRoute extends AbstractRoute {
    public static final String EDF_FILE_URL = "/eegFile";
    public static final String VIDEO_URL = "/video";
    private EegResource eegResource;

    public CreateContentOfEegRoute(EegResource _eegResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/content/[id]/EEG"));
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

                FileQueryObject fqo = toContentObject(bodyParameters, FileQueryObject.class);

                // If the content type is EEG, then the routes is a proxy to redirect the upload request to visio server.
                File file = null;

                try {
                    file = fqo.file().getFile();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                String url = BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/eeg/" + qo.id();

                if ("edf".equals(fqo.extension())) {
                    url += EDF_FILE_URL;
                } else {
                    url += VIDEO_URL;
                }

                // FIX ME : Here we use RestAssured which provides us a simple way to upload a file.
                given().
                        formParam("extension", fqo.extension()).
                        multiPart("file", file, fqo.extension()).
                        when().
                        post(url);

                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}