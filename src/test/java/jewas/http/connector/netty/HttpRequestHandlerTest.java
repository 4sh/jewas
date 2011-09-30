package jewas.http.connector.netty;

import com.jayway.restassured.RestAssured;
import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.data.HttpData;
import jewas.test.fakeapp.routes.model.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class HttpRequestHandlerTest {

    private static final int SERVER_PORT = 28086;

    private RestServer restServer = null;
    private RememberingCallsRoute rememberingCallsRoute = new RememberingCallsRoute("/rememberCall");

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
        restServer.addRoutes(
                rememberingCallsRoute);
        restServer.start();
        RestAssured.port = SERVER_PORT;
    }

    @After
    public void stopServer() {
        restServer.stop();
        restServer = null;
    }

    @Test
    public void shouldRequestHandlerCallbacksBeCalledOnlyOnceWithQueryParams(){
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
