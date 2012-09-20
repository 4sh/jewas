package jewas.http.session;

import jewas.http.HttpRequest;
import jewas.util.security.Tokens;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;

import java.io.Serializable;
import java.util.*;

/**
 * @author fcamblor
 * Quick and dirty implementation of session management based on a cookie
 * I'm not really proud of the way it is implemented (there are lots of improvements which could be brought,
 * particularly on thread safety and performance)
 */
public class Sessions {
    private static final Map<CookieSessionKey, Map<String, Serializable>> SESSIONS_BY_COOKIE =
            Collections.synchronizedMap(new HashMap<CookieSessionKey, Map<String, Serializable>>());

    private static final String SESSION_ID_COOKIE_KEY = "sessionId";

    public static Serializable get(HttpRequest request, String attributeName){
        Map<String, Serializable> session = get(request);
        return session.get(attributeName);
    }

    public static void set(HttpRequest request, String name, Serializable value) {
        get(request).put(name, value);
    }

    protected static Map<String, Serializable> get(HttpRequest request){
        cleanObsoleteSessions();

        Map<String, Serializable> session = null;

        Cookie sessionCookie = request.cookie(SESSION_ID_COOKIE_KEY);
        if(sessionCookie != null){
            String sessionId = sessionCookie.getValue();
            session = SESSIONS_BY_COOKIE.get(new CookieSessionKey(sessionId));
        }

        // If session cookie is not yet present, this is the first time we make a request on the site
        // => we should create the session cookie
        if(sessionCookie == null || session == null){
            String sessionId = generateUniqueSessionId();
            sessionCookie = new DefaultCookie(SESSION_ID_COOKIE_KEY, sessionId);
            request.addCookie(sessionCookie);
            // Initializing session
            session = new HashMap<>();
            SESSIONS_BY_COOKIE.put(new CookieSessionKey(sessionId), session);
        }

        return session;
    }

    private static void cleanObsoleteSessions() {
        List<CookieSessionKey> obsoleteKeys = new ArrayList<>();
        for(CookieSessionKey key : SESSIONS_BY_COOKIE.keySet()){
            if(key.expired()){
                obsoleteKeys.add(key);
            }
        }
        for(CookieSessionKey obsoleteKey : obsoleteKeys){
            SESSIONS_BY_COOKIE.remove(obsoleteKey);
        }
    }

    private static String generateUniqueSessionId() {
        List<String> existingSessionIds = new ArrayList<>();
        for(CookieSessionKey key : SESSIONS_BY_COOKIE.keySet()){
            existingSessionIds.add(key.cookieId());
        }
        return Tokens.generateUniqueToken(existingSessionIds);
    }
}
