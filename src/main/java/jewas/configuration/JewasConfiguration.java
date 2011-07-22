package jewas.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public abstract class JewasConfiguration {
    /**
     * The default application configuration file path to use.
     */
    private static final String APPLICATION_CONFIGURATION_FILE_PATH = "/conf/jewas.conf";

    /**
     * The key to use in the application configuration file to define the template folder.
     */
    private static final String TEMPLATE_PATH_KEY = "templates.path";

    /**
     * The key to use in the application configuration file to define the static resources folder.
     */
    private static final String STATIC_RESOURCES_PATH_KEY = "static.resources.path";

    /**
     * The delegate to use to get the properties.
     */
    protected static JewasConfigurationDelegate delegate =
            new DefaultJewasConfigurationDelegate(APPLICATION_CONFIGURATION_FILE_PATH);;

    /**
     *
     * @return the path of the templates folder.
     */
    public static String getTemplatesPath() {
        String path =  delegate.getProperties().getProperty(TEMPLATE_PATH_KEY);

        return path;
    }

    /**
     *
     * @return the path of the static resources folder.
     */
    public static String getStaticResourcesPath() {
        String path =  delegate.getProperties().getProperty(STATIC_RESOURCES_PATH_KEY);

        return path;
    }
}
