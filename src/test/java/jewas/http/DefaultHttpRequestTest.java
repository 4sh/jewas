package jewas.http;

import com.jayway.restassured.RestAssured;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

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
        private File file;
        private String param;
        public FileQueryObject file(File _file){
            this.file = _file;
            return this;
        }
        public File file(){
            return this.file;
        }
        public FileQueryObject param(String _param){
            this.param = _param;
            return this;
        }
        public String param(){
            return this.param;
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
                        final FileQueryObject qo = toQueryObject(parameters, FileQueryObject.class);
                        try {
                            retrievedFileContentsInRoute.add(FileUtils.readFileToString(qo.file()));
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot read uploaded file content", e);
                        }
                        retrievedParameterInRoute.add(qo.param());
                        return new RequestHandler() {
                           @Override
                            public void onRequest(HttpRequest request) {
                                request.respondHtml().content("ok");
                            }
                        };
                    }
                }
        );


        try {
            File fileToUpload = testFolder.newFile("fileToUpload");

            FileUtils.writeStringToFile(fileToUpload, String.format("Hello %nworld !"));

            Map<String,HttpTestUtils.FileUploadDescriptor> filesToUpload = new HashMap<String,HttpTestUtils.FileUploadDescriptor>();
            filesToUpload.put("file", new HttpTestUtils.FileUploadDescriptor(fileToUpload, "text/plain"));

            Map<String,String> params = new HashMap<String,String>();
            params.put("param", "foo");

            Class c = Files.class;
            
            String response = HttpTestUtils.sendMultipartFormTo("http://localhost:"+SERVER_PORT+"/foo",
                    filesToUpload, params);

            assertThat(retrievedFileContentsInRoute.size(), is(equalTo(1)));
            assertThat(retrievedParameterInRoute.size(), is(equalTo(1)));

            assertThat(retrievedParameterInRoute.get(0), is(equalTo("foo")));
            assertThat(retrievedFileContentsInRoute.get(0), is(equalTo(FileUtils.readFileToString(fileToUpload))));
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
