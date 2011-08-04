package jewas.http;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import jewas.configuration.JewasConfigurationForTest;
import jewas.test.fakeapp.routes.SimpleJSONFileRoute;
import jewas.test.fakeapp.routes.StaticResourceRoute;
import jewas.util.file.Files;
import junit.framework.Assert;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/15/11
 * Time: 1:42 PM
 */
public class RestServerTest {

    private static final int SERVER_PORT = 8086;

    private RestServer restServer = null;

    public RestServerTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(new SimpleJSONFileRoute());
        restServer.addRoutes(new StaticResourceRoute());
        restServer.start();
        RestAssured.port = SERVER_PORT;
    }

    @After
    public void stopServer() {
        restServer.stop();
        restServer = null;
    }


    @Test
    public void shouldAllowToStopThenRestartANewInstance() {
        // Stopping current running rest server instance
        restServer.stop();

        // Trying to start a new server instance on the same port
        // It should start normally...
        RestServer newRestServerInstance = RestServerFactory.createRestServer(SERVER_PORT);
        newRestServerInstance.start();
        newRestServerInstance.stop();

        // Restarting old server instance ... just for the @After to not fail
        restServer.start();
    }

    @Test
    public void shouldNotAllowToStart2InstancesOnTheSamePort() {
        try {
            RestServer newRestServerInstance = RestServerFactory.createRestServer(SERVER_PORT);
            newRestServerInstance.start();
            fail("2 instances on the same port should be forbidden !");
        } catch (AddressAlreadyInUseException e) {
            // It's ok if an exception is thrown ...
        }
    }

    @Test
    public void shouldUnregisteredRouteReturns404() throws IOException {
        expect().statusCode(404).when().get("/unregisteredRoute");
    }

    @Test
    public void shouldRenderingJSONWithGETParameterIsOk() {
        given().
                param("stringToConvert", "foo").
                expect().
                body("convertedString", CoreMatchers.is(CoreMatchers.equalTo("FOO"))).
                when().
                get("/root/toUpperCase/");

    }

    @Test
    public void shouldRenderingJSONWithURLParameterIsOk() {
        expect().
                body("convertedString", CoreMatchers.is(CoreMatchers.equalTo("FOO"))).
                when().
                get("/root/toUpperCase/foo");

    }

    @Test
    public void shouldMatchTheRouteWithTrailingSlash() {
        given().
                param("stringToConvert", "foo").
                expect().
                statusCode(200).when().get("/root/toUpperCase/////");
    }

    @Test
    public void shouldMatchTheRouteWithoutParamWithTrailingSlash() {
        given().
                param("stringToConvert", "foo").
                expect().
                statusCode(200).when().get("/root/toUpperCase/");
    }

    @Test
    public void shouldMatchTheRouteWithoutParamAndTrailingSlash() {
        given().
                param("stringToConvert", "foo").
                expect().
                statusCode(200).when().get("/root/toUpperCase");
    }

    @Test
    public void shouldUrlWithInlinedInterrogationPointParametersBeStripped(){
        expect()
                .statusCode(200)
                .body("convertedString", is(equalTo("FOO")))
                .when()
                .get("/root/toUpperCase?stringToConvert=foo");
    }

    @Ignore("urls with ; should be parsed correctly")
    @Test
    public void shouldUrlWithInlinedCommaParametersBeStripped(){
        expect()
                .statusCode(200)
                .body("convertedString", is(equalTo("FOO")))
                .when()
                .get("/root/toUpperCase;stringToConvert=foo");
    }

    @Test
    public void shouldReturnStaticResourceWithGETParameterIsOk() {
        //System.setProperty(JewasConfiguration.APPLICATION_CONFIGURATION_FILE_PATH_KEY, "jewas/configuration/jewasForHttp.conf");
        JewasConfigurationForTest.override("jewas/configuration/jewasForHttp.conf");
        Response response = get("/public/test.js");

        byte[] result = response.getBody().asByteArray();
        byte[] expected = new byte[0];

        try {
            expected = Files.getBytesFromStream(Files.getInputStreamFromPath("jewas/http/staticResources/test.js"));
        } catch (IOException e) {
            e.getMessage();
            Assert.assertTrue(e.getMessage(), false);
        }

        Assert.assertEquals(response.getBody().asString(), expected.length, result.length);

        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(result[i], expected[i]);
        }

        JewasConfigurationForTest.clean();
    }

    @Test
    public void shouldReturnEmptyStaticResourceWithGETParameterIsOk() {
        //System.setProperty(JewasConfiguration.APPLICATION_CONFIGURATION_FILE_PATH_KEY, "jewas/configuration/jewasForHttp.conf");
        JewasConfigurationForTest.override("jewas/configuration/jewasForHttp.conf");
        Response response = get("/public/emptyFile.js");

        byte[] result = response.getBody().asByteArray();
        byte[] expected = new byte[0];

        Assert.assertEquals(response.getBody().asString(), expected.length, result.length);

        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(result[i], expected[i]);
        }

        JewasConfigurationForTest.clean();
    }
}
