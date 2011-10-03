package jewas.http;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import jewas.configuration.JewasConfigurationForTest;
import jewas.routes.RedirectRoute;
import jewas.routes.StaticResourcesRoute;
import jewas.test.fakeapp.routes.FakeDeleteRoute;
import jewas.test.fakeapp.routes.FakePutRoute;
import jewas.test.fakeapp.routes.SimpleJSONFileRoute;
import jewas.util.file.Files;
import junit.framework.Assert;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

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

    private static final int SERVER_PORT = 28086;

    private RestServer restServer = null;
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public RestServerTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(
                new SimpleJSONFileRoute(),
                new StaticResourcesRoute("/public/", "jewas/http/staticResources/", testFolder.newFolder("resources")),
                new FakeDeleteRoute(),
                new FakePutRoute(),
                new RedirectRoute("/helloFoo", "/root/toUpperCase/foo"),
                new RedirectRoute("/", "/root/toUpperCase/foo")
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
                queryParam("stringToConvert", "foo").
                expect().
                body("convertedString", is(equalTo("FOO"))).
                when().
                get("/root/toUpperCase/");

    }

    @Test
    public void shouldRenderingJSONWithURLParameterIsOk() {
        expect().
                body("convertedString", is(equalTo("FOO"))).
                when().
                get("/root/toUpperCase/foo");

    }

    @Test
    public void shouldMatchTheRouteWithTrailingSlash() {
        given().
                queryParam("stringToConvert", "foo").
                expect().
                statusCode(200).when().get("/root/toUpperCase/////");
    }

    @Test
    public void shouldMatchTheRouteWithoutParamWithTrailingSlash() {
        given().
                queryParam("stringToConvert", "foo").
                expect().
                statusCode(200).when().get("/root/toUpperCase/");
    }

    @Test
    public void shouldMatchTheRouteWithoutParamAndTrailingSlash() {
        given().
                queryParam("stringToConvert", "foo").
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
    public void shouldUrlRedirectOnUrlHelloIsOk() {
        expect().
                body("convertedString", is(equalTo("FOO"))).
        when().
                get("/helloFoo");
    }

    @Test
    public void shouldPutMethodBeHandledCorrectly(){
        given().
                body("id=1234").
        expect().
                body("result", is(equalTo("putOk of 1234"))).
        when().
                put("/putThing");
    }

    @Test
    public void shouldMagicPutMethodBeHandledCorrectly(){
        given().
                body("id=1234").
        expect().
                body("result", is(equalTo("putOk of 1234"))).
        when().
                post("/putThing?__httpMethod=put");
    }

    @Test
    public void shouldDeleteMethodBeHandledCorrectly(){
        given().
                queryParam("id", "1234").
        expect().
                body("result", is(equalTo("deleteOk of 1234"))).
        when().
                delete("/deleteThing");
    }

    @Test
    public void shouldMagicDeleteMethodBeHandledCorrectly(){
        given().
                queryParam("id", "1234").
        expect().
                body("result", is(equalTo("deleteOk of 1234"))).
        when().
                post("/deleteThing?__httpMethod=delete");
    }

    @Test
    public void shouldUrlRedirectOnUrlRootIsOk() {
        expect().
                body("convertedString", is(equalTo("FOO"))).
                when().
                get("/");

    }

    @Test
    public void shouldReturnStaticResourceWithGETParameterIsOk() {
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
