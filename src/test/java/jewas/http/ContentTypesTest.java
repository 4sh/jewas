package jewas.http;

import com.jayway.restassured.RestAssured;
import jewas.configuration.JewasConfigurationForTest;
import jewas.routes.RedirectRoute;
import jewas.routes.StaticResourceRoute;
import jewas.test.fakeapp.routes.SimpleJSONFileRoute;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

/**
 * @author fcamblor
 */
public class ContentTypesTest {

    private static final int SERVER_PORT = 8086;

    private RestServer restServer = null;

    public ContentTypesTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(
                new StaticResourceRoute()
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
        JewasConfigurationForTest.override("jewas/configuration/jewasForHttp.conf");
        expect().
            contentType("application/javascript").
        when().
            get("/public/test.js");
    }
}
