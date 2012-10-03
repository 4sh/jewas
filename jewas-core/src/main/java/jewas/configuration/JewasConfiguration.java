package jewas.configuration;

import jewas.util.properties.ChainedProperties;

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
     * The current server name viewed by the client
     */
    public static final String SERVER_NAME = "server.name";

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
}
