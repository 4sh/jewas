package jewas.http;

import com.jayway.restassured.RestAssured;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import jewas.http.data.BodyParameters;
import jewas.http.data.FileUpload;
import jewas.http.data.FormBodyParameters;
import jewas.http.data.HttpData;
import jewas.http.impl.AbstractRequestHandler;
import jewas.http.util.HttpTestUtils;
import jewas.routes.RedirectRoute;
import jewas.routes.StaticResourcesRoute;
import jewas.test.fakeapp.routes.FakeDeleteRoute;
import jewas.test.fakeapp.routes.FakePutRoute;
import jewas.test.fakeapp.routes.SimpleJSONFileRoute;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author fcamblor
 */
public class DefaultHttpRequestTest {

    private static final int SERVER_PORT = 8086;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public DefaultHttpRequestTest() {
    }

    public static class FileQueryObject{
        private FileUpload fileupload;
        /* mixing fileupload paran & form parameter doesn't work for the moment...
        private String param;
        public FileQueryObject param(String _param){
            this.param = _param;
            return this;
        }
        public String param(){
            return this.param;
        }
        */
        public FileQueryObject fileupload(FileUpload _fileupload){
            this.fileupload = _fileupload;
            return this;
        }

        public FileUpload fileupload(){
            return this.fileupload;
        }
    }

    @Before
    public void setUp(){
        RestAssured.port = SERVER_PORT;
    }

    @Test
    public void shouldFileUploadBeHandledCorrectly() throws IOException {
        final List<String> retrievedFileContentsInRoute = new ArrayList<String>();
        final List<String> retrievedParameterInRoute = new ArrayList<String>();
        List<Route> routes = new ArrayList<Route>();
        RestServer server = startServer(
                new AbstractRoute(HttpMethodMatcher.ALL, new PatternUriPathMatcher("/foo")) {
                    @Override
                    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
                        return new AbstractRequestHandler() {
                            
                            @Override
                            public void onReady(HttpRequest request, BodyParameters parameters) {
                                final FileQueryObject qo = toContentObject(parameters, FileQueryObject.class);
                                try {
                                    File uploadedFile = testFolder.newFile("uploadedFile");
                                    qo.fileupload().toFile(uploadedFile);
                                    retrievedFileContentsInRoute.add(FileUtils.readFileToString(uploadedFile));
                                } catch (IOException e) {
                                    throw new RuntimeException("Cannot read uploaded file content", e);
                                }
                                //mixing fileupload paran & form parameter doesn't work for the moment...
                                //retrievedParameterInRoute.add(qo.param());
                                request.respondHtml().content("ok");
                            }
                        };
                    }
                }
        );


        try {
            File fileToUpload = new File(jewas.util.file.Files.getResourceFromPath(this.getClass(), "jewas/upload/rest-assured-khelg-2011.pdf").getFile());

/*
            Map<String,HttpTestUtils.FileUploadDescriptor> filesToUpload = new HashMap<String,HttpTestUtils.FileUploadDescriptor>();
            filesToUpload.put("fileupload", new HttpTestUtils.FileUploadDescriptor(fileToUpload, ContentType.APP_PDF.value()));

            Map<String,String> params = new HashMap<String,String>();
            params.put("param", "foo");

            String response = HttpTestUtils.sendMultipartFormTo("http://localhost:"+SERVER_PORT+"/foo",
                    filesToUpload, params);
*/
            given().
                    formParam("param", "foo").
                    multiPart("fileupload", fileToUpload, ContentType.APP_PDF.value()).
                    multiPart("fileupload2", fileToUpload, ContentType.APP_PDF.value()).
            when().
                    post("/foo");

            assertThat(retrievedFileContentsInRoute.size(), is(equalTo(1)));
            assertThat(retrievedFileContentsInRoute.get(0), is(equalTo(FileUtils.readFileToString(fileToUpload))));

//mixing fileupload paran & form parameter doesn't work for the moment...
//            assertThat(retrievedParameterInRoute.size(), is(equalTo(1)));
//            assertThat(retrievedParameterInRoute.get(0), is(equalTo("foo")));

        }finally{
            server.stop();
        }
    }

    protected static RestServer startServer(Route... routes){
        // Restserver without any route
        RestServer restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(routes);
        restServer.start();
        return restServer;
    }

}
