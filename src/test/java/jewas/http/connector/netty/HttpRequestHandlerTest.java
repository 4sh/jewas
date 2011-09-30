package jewas.http.connector.netty;

import com.jayway.restassured.RestAssured;
import jewas.collection.TypedArrayList;
import jewas.collection.TypedList;
import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.data.FileUpload;
import jewas.http.data.HttpData;
import jewas.http.impl.AbstractRequestHandler;
import jewas.test.fakeapp.routes.model.Result;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class HttpRequestHandlerTest {

    private static final int SERVER_PORT = 28086;

    private RestServer restServer = null;
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private static class RememberingCallsRoute extends AbstractRoute {
        public int numberOfOnRequestCalls = 0;
        public int numberOfOfferCalls = 0;
        public int numberOfOnReadyCalls = 0;
        public int numberOfOnMatchCalls = 0;

        public RememberingCallsRoute(String uriPattern){
            super(HttpMethodMatcher.ALL, uriPattern);
        }

        @Override
        protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
            numberOfOnMatchCalls++;
            return new RequestHandler() {
                @Override
                public void onRequest(HttpRequest request) {
                    numberOfOnRequestCalls++;
                }

                @Override
                public void offer(HttpRequest request, HttpData data) {
                    numberOfOfferCalls++;
                }

                @Override
                public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                    numberOfOnReadyCalls++;
                    request.respondJson().object(new Result().result("ok"));
                }
            };
        }
    }

    public HttpRequestHandlerTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.start();
        RestAssured.port = SERVER_PORT;
    }

    @After
    public void stopServer() {
        restServer.stop();
        restServer = null;
    }

    public static class FileQueryObject{
        private FileUpload fileupload;
        private FileUpload fileupload2;
        private String param;
        public FileQueryObject param(String _param){
            this.param = _param;
            return this;
        }
        public String param(){
            return this.param;
        }
        public FileQueryObject fileupload2(FileUpload _fileupload2){
            this.fileupload2 = _fileupload2;
            return this;
        }
        public FileUpload fileupload2(){
            return this.fileupload2;
        }
        public FileQueryObject fileupload(FileUpload _fileupload){
            this.fileupload = _fileupload;
            return this;
        }
        public FileUpload fileupload(){
            return this.fileupload;
        }
    }

    public static class MultipleValuesObject {
        // TypedLists MUST be instantiated during construction !
        private TypedList<Long> values = new TypedArrayList<Long>(Long.class);
        public MultipleValuesObject values(TypedList<Long> _values){
            this.values = _values;
            return this;
        }
        public TypedList<Long> values(){
            return this.values;
        }
    }

    @Test
    public void shouldFileUploadBeHandledCorrectly() throws IOException {
        final List<String> retrievedFileContentsInRoute = new ArrayList<String>();
        final List<String> retrievedParameterInRoute = new ArrayList<String>();
        List<Route> routes = new ArrayList<Route>();
        restServer.addRoutes(
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
                                    File uploadedFile2 = testFolder.newFile("uploadedFile2");
                                    qo.fileupload2().toFile(uploadedFile2);
                                    retrievedFileContentsInRoute.add(FileUtils.readFileToString(uploadedFile2));
                                } catch (IOException e) {
                                    throw new RuntimeException("Cannot read uploaded file content", e);
                                }
                                retrievedParameterInRoute.add(qo.param());
                                request.respondHtml().content("ok");
                            }
                        };
                    }
                }
        );


        File fileToUpload = new File(jewas.util.file.Files.getResourceFromPath(this.getClass(), "jewas/upload/rest-assured-khelg-2011.pdf").getFile());
        File fileToUpload2 = new File(jewas.util.file.Files.getResourceFromPath(this.getClass(), this.getClass().getCanonicalName().replaceAll("\\.", "/")+".class").getFile());

        given().
                formParam("param", "foo").
                multiPart("fileupload", fileToUpload, ContentType.APP_PDF.value()).
                multiPart("fileupload2", fileToUpload2, ContentType.APP_PDF.value()).
        when().
                post("/foo");

        assertThat(retrievedFileContentsInRoute.size(), is(equalTo(2)));
        assertThat(retrievedFileContentsInRoute.get(0), is(equalTo(FileUtils.readFileToString(fileToUpload))));
        assertThat(retrievedFileContentsInRoute.get(1), is(equalTo(FileUtils.readFileToString(fileToUpload2))));

        assertThat(retrievedParameterInRoute.size(), is(equalTo(1)));
        assertThat(retrievedParameterInRoute.get(0), is(equalTo("foo")));
    }

    @Test
    public void shouldMultipleValuesInParametersBeHandledCorrectlyInContentObjects(){
        final List<Long> retrievedParameterInRoute = new ArrayList<Long>();
        List<Route> routes = new ArrayList<Route>();
        restServer.addRoutes(
                new AbstractRoute(HttpMethodMatcher.ALL, new PatternUriPathMatcher("/foo")) {
                    @Override
                    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
                        return new AbstractRequestHandler() {

                            @Override
                            public void onReady(HttpRequest request, BodyParameters parameters) {
                                final MultipleValuesObject qo = toContentObject(parameters, MultipleValuesObject.class);
                                retrievedParameterInRoute.addAll(qo.values());
                                request.respondHtml().content("ok");
                            }
                        };
                    }
                }
        );


        given().
                formParam("values", "10").
                formParam("values", "20").
        when().
                post("/foo");

        assertThat(retrievedParameterInRoute.size(), is(equalTo(2)));
        assertThat(retrievedParameterInRoute.get(0), is(equalTo(Long.valueOf(10))));
        assertThat(retrievedParameterInRoute.get(1), is(equalTo(Long.valueOf(20))));
    }

    @Test
    public void shouldMultipleFileUploadsInParametersBeHandledCorrectlyInContentObjects() throws IOException {
        final List<String> retrievedFileContentsInRoute = new ArrayList<String>();
        List<Route> routes = new ArrayList<Route>();
        restServer.addRoutes(
                new AbstractRoute(HttpMethodMatcher.ALL, new PatternUriPathMatcher("/foo")) {
                    @Override
                    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
                        return new AbstractRequestHandler() {

                            @Override
                            public void onReady(HttpRequest request, BodyParameters parameters) {
                                FileQueryObject valuedObject = toContentObject(parameters, FileQueryObject.class);
                                try {
                                    for(int i=0; i<valuedObject.fileupload().count(); i++){
                                        File uploadedFile = testFolder.newFile("uploadedFile");
                                        valuedObject.fileupload().toFile(i, uploadedFile);
                                        retrievedFileContentsInRoute.add(FileUtils.readFileToString(uploadedFile));
                                        uploadedFile.delete();
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException("Cannot read uploaded file content", e);
                                }
                                request.respondHtml().content("ok");
                            }
                        };
                    }
                }
        );

        File fileToUpload = new File(jewas.util.file.Files.getResourceFromPath(this.getClass(), "jewas/upload/rest-assured-khelg-2011.pdf").getFile());
        File fileToUpload2 = new File(jewas.util.file.Files.getResourceFromPath(this.getClass(), this.getClass().getCanonicalName().replaceAll("\\.", "/")+".class").getFile());

        given().
                multiPart("fileupload", fileToUpload, ContentType.APP_PDF.value()).
                multiPart("fileupload", fileToUpload2).
        when().
                post("/foo");

        assertThat(retrievedFileContentsInRoute.size(), is(equalTo(2)));
        assertThat(retrievedFileContentsInRoute.get(0), is(equalTo(FileUtils.readFileToString(fileToUpload))));
        assertThat(retrievedFileContentsInRoute.get(1), is(equalTo(FileUtils.readFileToString(fileToUpload2))));
    }

    @Test
    public void shouldRequestHandlerCallbacksBeCalledOnlyOnceWithQueryParams(){
        RememberingCallsRoute rememberingCallsRoute = new RememberingCallsRoute("/rememberCall");
        restServer.addRoutes(rememberingCallsRoute);

        given().
                queryParam("id", "12345").
        expect().
                body("result", is(equalTo("ok"))).
        when().
                put("/rememberCall");

        assertThat(rememberingCallsRoute.numberOfOnMatchCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOfferCalls, is(equalTo(0)));
        assertThat(rememberingCallsRoute.numberOfOnReadyCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOnRequestCalls, is(equalTo(1)));
    }

    @Test
    public void shouldRequestHandlerCallbacksBeCalledOnlyOnceWithBodyParams(){
        RememberingCallsRoute rememberingCallsRoute = new RememberingCallsRoute("/rememberCall");
        restServer.addRoutes(rememberingCallsRoute);

        given().
                body("id=1234").
        expect().
                body("result", is(equalTo("ok"))).
        when().
                put("/rememberCall");

        assertThat(rememberingCallsRoute.numberOfOnMatchCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOfferCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOnReadyCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOnRequestCalls, is(equalTo(1)));
    }

    @Test
    public void shouldRequestHandlerCallbacksBeCalledOnlyOnceWithMultiPartParams() throws IOException {
        RememberingCallsRoute rememberingCallsRoute = new RememberingCallsRoute("/rememberCall");
        restServer.addRoutes(rememberingCallsRoute);

        File fileToUpload = new File(jewas.util.file.Files.getResourceFromPath(this.getClass(), "jewas/upload/rest-assured-khelg-2011.pdf").getFile());
        given().
                multiPart("fileupload", fileToUpload, ContentType.APP_PDF.value()).
        when().
                post("/rememberCall");

        assertThat(rememberingCallsRoute.numberOfOnMatchCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOfferCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOnReadyCalls, is(equalTo(1)));
        assertThat(rememberingCallsRoute.numberOfOnRequestCalls, is(equalTo(1)));
    }
}
