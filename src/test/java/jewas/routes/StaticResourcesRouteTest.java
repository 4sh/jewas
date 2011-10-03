package jewas.routes;

import com.jayway.restassured.RestAssured;
import jewas.http.RestServer;
import jewas.http.RestServerFactory;
import jewas.util.file.Closeables;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public StaticResourcesRouteTest() {
    }

    @Before
    public void startServer() {
        // Restserver without any route
        restServer = RestServerFactory.createRestServer(SERVER_PORT);
        restServer.addRoutes(
                new StaticResourcesRoute("/pub/", "public/", testFolder.newFolder("resources")));
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
        // Let's gather a second time the same file, filesystem cache should be used !
        byte[] remoteFile2 = readBytesFromInputStream(new URL(remoteUrl).openStream());

        // This path is located into fr.4sh.jewas.tests:resourcesInJarForTests artefact provided in the classpath
        final String expectedResourcePath = "public/images/bbeeg/problematicResource.jpg";
        byte[] exptectedFile = readBytesFromInputStream(this.getClass().getClassLoader().getResource(expectedResourcePath).openStream());

        assertNotNull(remoteFile);
        assertNotNull(remoteFile2);
        assertFalse(remoteFile.length == 0);
        assertFalse(remoteFile2.length == 0);

        assertThat(remoteFile.length, is(equalTo(exptectedFile.length)));
        assertThat(remoteFile2.length, is(equalTo(exptectedFile.length)));
        assertThat(remoteFile, is(equalTo(exptectedFile)));
        assertThat(remoteFile2, is(equalTo(exptectedFile)));
    }

    private static byte[] readBytesFromInputStream(InputStream is){
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        try {
            byte[] byteChunk = new byte[4096];
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                bais.write(byteChunk, 0, n);
            }
        } catch (IOException e) { throw new RuntimeException(e);
        } finally {
            Closeables.defensiveClose(is);
        }
        return bais.toByteArray();
    }
}
