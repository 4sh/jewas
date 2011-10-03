package jewas.configuration;

import jewas.util.file.Closeables;
import jewas.util.file.Files;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 08:49
 *
 * The default implementation fo {@link JewasConfigurationDelegate}.
 */
public class DefaultJewasConfigurationDelegate implements JewasConfigurationDelegate {

    /**
     * The properties that have been loaded from the path.
     */
    private Properties properties;

    public DefaultJewasConfigurationDelegate(String path) {
        properties = new Properties();

        InputStream is = null;
        try {
            is = Files.getInputStreamFromPath(path);
            properties.load(is);
        } catch (IOException e) {
            // FIXME : log a warning here !... Or even throw a non checked exception ???
            System.err.println(e.getMessage());
        } finally {
            Closeables.defensiveClose(is);
        }
    }

    public Properties getProperties() {
       return properties;
    }
}
