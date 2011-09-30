package jewas.routes;

import com.jayway.restassured.RestAssured;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class StaticResourcesRouteTest {


    private static final int SERVER_PORT = 28086;

    private RestServer restServer = null;

    public StaticResourcesRouteTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(
                new StaticResourcesRoute("/pub/", "public/"));
        restServer.start();
        RestAssured.port = SERVER_PORT;
    }

    @After
    public void stopServer() {
        restServer.stop();
        restServer = null;
    }

    @Test
    public void shouldSpecialFilesResidingInJarBeHandledCorrectly() throws IOException {
        final String remoteUrl = "http://localhost:"+SERVER_PORT+"/pub/images/bbeeg/problematicResource.jpg";
        byte[] remoteFile = readBytesFromInputStream(new URL(remoteUrl).openStream());

        final String expectedResourcePath = "public/images/bbeeg/problematicResource.jpg";
        byte[] exptectedFile = readBytesFromInputStream(this.getClass().getClassLoader().getResource(expectedResourcePath).openStream());

        assertNotNull(exptectedFile);
        assertFalse(exptectedFile.length == 0);

        assertThat(remoteFile.length, is(equalTo(exptectedFile.length)));
        assertThat(remoteFile, is(equalTo(exptectedFile)));
    }

    private static byte[] readBytesFromInputStream(InputStream is){
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        try {
          byte[] byteChunk = new byte[4096];
          int n;

          while ( (n = is.read(byteChunk)) > 0 ) {
            bais.write(byteChunk, 0, n);
          }
        } catch (IOException e) { throw new RuntimeException(e); }
        finally {
          if (is != null) { try { is.close(); } catch(IOException e) { throw new RuntimeException(e); }}
        }
        return bais.toByteArray();
    }
}
