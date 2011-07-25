package jewas.configuration;

import jewas.configuration.JewasConfigurationDelegate;
import jewas.util.file.Files;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 09:13
 *
 * The implement of {@link JewasConfigurationDelegate} to use for test.
 */
public class JewasConfigurationDelegateForTest implements JewasConfigurationDelegate{
    private static Properties properties;

    /**
     * The stackTrace to know the called hierarchy that instanciate this object.
     */
    public StackTraceElement[] stacktrace;

    @Override
    public Properties getProperties() {
        return properties;
    }

    /**
     * Load new properties from the given path.
     * @param path the path
     */
    public static void override(String path) {
        properties = new Properties();

        try {
            properties.load(new FileInputStream(Files.getFileFromPath(path)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Clean the properties.
     */
    public static void clean() {
        properties = null;
    }
}
