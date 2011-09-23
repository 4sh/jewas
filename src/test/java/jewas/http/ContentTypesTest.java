package jewas.http;

import com.jayway.restassured.RestAssured;
import jewas.routes.StaticResourcesRoute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;

/**
 * @author fcamblor
 */
public class ContentTypesTest {

    private static final int SERVER_PORT = 28086;

    private RestServer restServer = null;

    public ContentTypesTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(
                new StaticResourcesRoute("/public/", "jewas/http/staticResources/")
        );
        restServer.start();
        RestAssured.port = SERVER_PORT;
    }

    @After
    public void stopServer() {
        restServer.stop();
        restServer = null;
    }

    @Test
    public void shouldJsUriBeOfJavascriptContentType(){
        expect().
            contentType("application/javascript").
        when().
            get("/public/test.js");
    }
}
