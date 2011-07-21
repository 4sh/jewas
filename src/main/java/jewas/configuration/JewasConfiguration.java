package jewas.configuration;

import jewas.util.file.Files;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class JewasConfiguration {
    /**
     * The key to use to define a system property containing the path of the application configuration file.
     */
    public static final String APPLICATION_CONFIGURATION_FILE_PATH_KEY = "APPLICATION_CONFIGURATION_FILE_PATH_KEY";

    /**
     * The default application configuration file path to use in DEV mode.
     */
    private static final String DEV_APPLICATION_CONFIGURATION_FILE_PATH = "/conf/dev/jewas.conf";

    // TODO: Add all the keys managed in the jewas.conf file.
    /**
     * The key to use in the application configuration file to define the template folder.
     */
    private static final String TEMPLATE_PATH_KEY = "templates.path";

    /**
     * The key to use in the application configuration file to define the static resources folder.
     */
    private static final String STATIC_RESOURCES_PATH_KEY = "static.resources.path";

    /**
     * The properties that have been loaded from the application configuration file.
     */
    private static Properties properties;

    /**
     * Init the properties from the application configuration file if not already done.
     */
    private static void initPropertiesIfNeeded() {
        if (properties != null) {
            return;
        }

        properties = new Properties();

        String path;
        String property = System.getProperty(APPLICATION_CONFIGURATION_FILE_PATH_KEY);

        if (property != null && !"".equals(property)) {
            path = property;
        } else {
            // TODO: Add a test to check the mode (DEV or PROD).
            path= DEV_APPLICATION_CONFIGURATION_FILE_PATH;
        }

        try {
            properties.load(new FileInputStream(Files.getFileFromPath(path)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     * @return the path of the templates folder.
     */
    public static String getTemplatesPath() {
        initPropertiesIfNeeded();

        String path =  properties.getProperty(TEMPLATE_PATH_KEY);

        return path;
    }

    /**
     *
     * @return the path of the static resources folder.
     */
    public static String getStaticResourcesPath() {
        initPropertiesIfNeeded();

        String path =  properties.getProperty(STATIC_RESOURCES_PATH_KEY);

        return path;
    }
}
