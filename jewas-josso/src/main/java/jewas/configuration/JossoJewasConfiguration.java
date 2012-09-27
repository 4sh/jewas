package jewas.configuration;

/**
 * @author fcamblor
 * Utility class allowing to retrieve special Jewas configuration properties relating to JOSSO
 */
public class JossoJewasConfiguration {
    /**
     * The josso back url where josso will redirect back after a successful login
     */
    private static final String JOSSO_BACK_URL_KEY = "josso.back.url";

    /**
     * Current josso partner app name
     */
    private static final String JOSSO_PARTNER_APP_NAME = "josso.partner.app.name";

    /**
     * Josso gateway server name
     */
    private static final String JOSSO_GATEWAY_SERVER_NAME = "josso.gateway.server.name";

    /**
     * Josso login url
     */
    private static final String JOSSO_GATEWAY_LOGIN_URL = "josso.login.url";

    /**
     * Josso logout url
     */
    private static final String JOSSO_GATEWAY_LOGOUT_URL = "josso.logout.url";

    public static String jossoBackUrl() {
        return JewasConfiguration.getValueOfKeyOrDefaultValue(JOSSO_BACK_URL_KEY, null);
    }

    public static String partnerAppName() {
        return JewasConfiguration.getValueOfKeyOrDefaultValue(JOSSO_PARTNER_APP_NAME, null);
    }

    public static String gatewayServerName(){
        return JewasConfiguration.getValueOfKeyOrDefaultValue(JOSSO_GATEWAY_SERVER_NAME, null);
    }

    public static String gatewayLoginUrl(){
        return JewasConfiguration.getValueOfKeyOrDefaultValue(JOSSO_GATEWAY_LOGIN_URL, null);
    }

    public static String gatewayLogoutUrl(){
        return JewasConfiguration.getValueOfKeyOrDefaultValue(JOSSO_GATEWAY_LOGOUT_URL, null);
    }
}
