package jewas.routes.security.josso;

import jewas.http.HttpRequest;
import jewas.http.session.Sessions;

import java.io.Serializable;

/**
 * @author fcamblor
 *         Utility class to set and retrieve JOSSO User in session
 */
public class JossoUser {
    private static final String KEY_USER_INFOS = "jewas.user.infos";

    public static Serializable get(HttpRequest request) {
        return Sessions.get(request).get(KEY_USER_INFOS);
    }

    public static void set(HttpRequest request, Serializable userInfos) {
        Sessions.get(request).set(KEY_USER_INFOS, userInfos);
    }

    public static void remove(HttpRequest request) {
        Sessions.get(request).remove(KEY_USER_INFOS);
    }

    public static boolean sessionOpened(HttpRequest request) {
        return get(request) != null;
    }
}
