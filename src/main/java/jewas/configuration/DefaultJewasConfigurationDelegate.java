package jewas.configuration;

import jewas.util.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Class logger.
     */
    private Logger logger = LoggerFactory.getLogger(DefaultJewasConfigurationDelegate.class);

    /**
     * The properties that have been loaded from the path.
     */
    private Properties properties;

    public DefaultJewasConfigurationDelegate(String path) {
        properties = new Properties();

        try(InputStream is = Files.getInputStreamFromPath(path))
        {
            properties.load(is);
        } catch (IOException e) {
            logger.error("Cannot get input stream from: " + path);
        }
    }

    public Properties getProperties() {
       return properties;
    }
}
