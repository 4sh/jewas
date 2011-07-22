package jewas.configuration;

import jewas.util.file.Files;

import java.io.FileInputStream;
import java.io.IOException;
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

        try {
            properties.load(new FileInputStream(Files.getFileFromPath(path)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Properties getProperties() {
       return properties;
    }
}
