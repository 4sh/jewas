package jewas.configuration;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 09:13
 * <p/>
 * The implement of {@link JewasConfigurationDelegate} to use for test.
 */
public class JewasConfigurationDelegateForTest implements JewasConfigurationDelegate {
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
     *
     * @param props the properties to override
     */
    public static void override(Properties props) {
        properties = props;
    }

    /**
     * Clean the properties.
     */
    public static void clean() {
        properties = null;
    }
}
