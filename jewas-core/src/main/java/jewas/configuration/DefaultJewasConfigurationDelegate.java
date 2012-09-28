package jewas.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 08:49
 * <p/>
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

    public DefaultJewasConfigurationDelegate(Properties props) {
        properties = props;
    }

    public Properties getProperties() {
        return properties;
    }
}
