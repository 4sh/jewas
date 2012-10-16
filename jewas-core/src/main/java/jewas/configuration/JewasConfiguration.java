package jewas.configuration;

import jewas.util.properties.ChainedProperties;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 11:10
 */
public abstract class JewasConfiguration {
    /**
     * The key to use in the application configuration file to define the template folder.
     */
    private static final String TEMPLATE_PATH_KEY = "templates.path";

    /**
     * The default value of the template folder.
     */
    private static final String TEMPLATE_PATH_DEFAULT_VALUE = "templates/";

    /**
     * Key for the temporary cached resources directory jewas will use to cache resources on filesystem
     * Path for an empty directory where cached static resource files will be extracted
     */
    private static final String CACHED_RESOURCES_DIRECTORY_KEY = "cached.resources.directory";

    /**
     * The current server name viewed by the client
     */
    public static final String SERVER_NAME = "server.name";

    /**
     * Development mode
     */
    public static final String DEV_MODE = "dev.mode";

    /**
     * The delegate to use to get the properties.
     */
    protected static JewasConfigurationDelegate delegate =
            new DefaultJewasConfigurationDelegate(
                    new ChainedProperties()
                            // WARNING : If this section is updated, think about updating josso-agent.config.xml file accordingly !
                            .chainProperties("conf/jewas.properties", "jewas-global", true)
                            .chainProperties("conf/envspecific/jewas-${deploy.target.env}.properties", "jewas-env", false)
                            .chainProperties("conf/devspecific/jewas-${user.name}.properties", "jewas-user", false)
                            .load()
            );

    /**
     * Get the value of the given key if defined, else the default value.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the value of the given key if defined, else the default value.
     */
    static String getValueOfKeyOrDefaultValue(String key, String defaultValue) {
        String value = delegate.getProperties().getProperty(key);

        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * @return the path of the templates folder.
     */
    public static String getTemplatesPath() {
        return getValueOfKeyOrDefaultValue(TEMPLATE_PATH_KEY, TEMPLATE_PATH_DEFAULT_VALUE);
    }

    /**
     * Public server name of current app
     */
    public static String serverName() {
        return JewasConfiguration.getValueOfKeyOrDefaultValue(SERVER_NAME, null);
    }

    public static String contextPath() {
        // In jewas, there isn't any context path
        return "/";
    }

    public static boolean devMode() {
        return Boolean.valueOf(getValueOfKeyOrDefaultValue(DEV_MODE, Boolean.FALSE.toString()));
    }

    public static File cachedResourcesDirectory() {
        String cachedResourcesPath = getValueOfKeyOrDefaultValue(CACHED_RESOURCES_DIRECTORY_KEY, null);
        if (cachedResourcesPath == null) {
            return null;
        }
        Path cachedResourcesDirectory = Paths.get(cachedResourcesPath);
        return cachedResourcesDirectory.toFile();
    }
}
