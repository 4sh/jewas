package jewas.configuration;

/**
 * @author fcamblor
 */
public class JossoJewasConfiguration {
    /**
     * The josso back url where josso will redirect back after a successful login
     */
    public static final String JOSSO_BACK_URL_KEY = "josso.back.url";

    public static String jossoBackUrl() {
        return JewasConfiguration.getValueOfKeyOrDefaultValue(JOSSO_BACK_URL_KEY, null);
    }
}
