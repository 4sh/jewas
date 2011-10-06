package fr.fsh.bbeeg.content.routes;

import com.jayway.restassured.response.Response;
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
import jewas.json.Json;

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
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/content/EEG/[id]"));
        eegResource = _eegResource;
    }

    public static class SuccessFileUploadObject extends SuccessObject {
        private String fileId;

        public SuccessFileUploadObject fileId(String _fileId){
            this.fileId = _fileId;
            return this;
        }

        public String fileId(){
            return this.fileId;
        }
    }


    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId objectId = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                FileQueryObject fqo = toContentObject(bodyParameters, FileQueryObject.class);

                // As the content type is EEG, then the route is a proxy to redirect the upload request to visio server.
                File file = null;

                try {
                    file = fqo.file().getFile();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                String url = BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/eeg";

                if ("edf".equals(fqo.extension())) {
                    url += EDF_FILE_URL;
                } else {
                    url += VIDEO_URL;
                }

                if (objectId.id() != null) {
                    url += "/" + objectId.id().toString();
                }

                // FIX ME : Here we use RestAssured which provides us a simple way to upload a file.
                Response response = given().
                        formParam("extension", fqo.extension()).
                        multiPart("file", file, fqo.extension()).
                        when().
                        post(url);

                SuccessFileUploadObject sfuo = (SuccessFileUploadObject)
                        Json.instance().fromJsonString(response.getBody().asString(), SuccessFileUploadObject.class);

                request.respondJson().object(sfuo);
            }
        };
    }
}