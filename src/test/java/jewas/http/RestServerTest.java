package jewas.http;

import com.jayway.restassured.RestAssured;
import jewas.test.util.RestServerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/15/11
 * Time: 1:42 PM
 */
public class RestServerTest {

    private static final int SERVER_PORT = 8086;

    private RestServer restServer = null;

    public RestServerTest(){
    }

    @Before
    public void startServer(){
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.start();
        RestAssured.port = SERVER_PORT;
    }

    @After
    public void stopServer(){
        restServer.stop();
        restServer = null;
    }


    @Test
    public void shouldAllowToStopThenRestartANewInstance(){
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
    public void shouldNotAllowToStart2InstancesOnTheSamePort(){
        try {
            RestServer newRestServerInstance = RestServerFactory.createRestServer(SERVER_PORT);
            newRestServerInstance.start();
            fail("2 instances on the same port should be forbidden !");
        }catch(AddressAlreadyInUseException e){
            // It's ok if an exception is thrown ...
        }
    }

    @Test
    public void shouldUnregisteredRouteReturns404() throws IOException {
        expect().statusCode(404).when().get("/unregisteredRoute");
    }
}
