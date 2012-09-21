package jewas.http.session;

import jewas.http.HttpRequest;
import jewas.util.security.Tokens;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fcamblor
 * Quick and dirty implementation of session management based on a cookie
 * I'm not really proud of the way it is implemented (there are lots of improvements which could be brought,
 * particularly on thread safety and performance)
 * TODO: Add SESSIONS_BY_COOKIE serialization on netty server stop + deserialization in a static block
 */
public class Sessions {
    private static final ConcurrentMap<CookieSessionKey, ConcurrentMap<String, Serializable>> SESSIONS_BY_COOKIE =
            new ConcurrentHashMap<>();

    private static final String SESSION_ID_COOKIE_KEY = "sessionId";

    public static Serializable get(HttpRequest request, String attributeName){
        Map<String, Serializable> session = get(request);
        return session.get(attributeName);
    }

    public static void set(HttpRequest request, String name, Serializable value) {
        Map<String, Serializable> session = get(request);
        session.put(name, value);
    }

    /**
     * Will retrieve session map for given request
     */
    protected static Map<String, Serializable> get(HttpRequest request){
        // On every get, we should ensure there isn't any obsolete key in static session map
        cleanObsoleteSessions();

        ConcurrentMap<String, Serializable> session = null;

        // Is there a session id sent in the request ?
        // If this is the case, we should try to retrieve corresponding session map
        // The retrieved session map could be null if, for instance, it was previously cleaned
        Cookie requestSessionCookie = request.cookie(SESSION_ID_COOKIE_KEY);
        if(requestSessionCookie != null){
            String sessionId = requestSessionCookie.getValue();
            session = SESSIONS_BY_COOKIE.get(new CookieSessionKey(sessionId));
        }

        // If session is not yet present, this is the first time we make a request on the site
        // => we should create the session cookie and initialize the session map
        if(session == null){
            String sessionId = generateUniqueSessionId();
            // Initializing session
            session = new ConcurrentHashMap<>();
            SESSIONS_BY_COOKIE.putIfAbsent(new CookieSessionKey(sessionId), session);
            // Storing cookie in request (for subsquent calls to get() in current request) and response
            // (for future requests)
            Cookie sessionCookie = new DefaultCookie(SESSION_ID_COOKIE_KEY, sessionId);
            request.addResponseCookie(sessionCookie);
            request.addRequestCookie(sessionCookie);
        }

        return session;
    }

    /**
     * Simple implementation will browser every keys, see if they are expired and remove it if this is the case
     * The implementation is not really efficient since it browse every keys, but don't know how to have a better
     * implementation
     */
    private static void cleanObsoleteSessions() {
        List<CookieSessionKey> obsoleteKeys = new ArrayList<>();
        for(CookieSessionKey key : SESSIONS_BY_COOKIE.keySet()){
            if(key.expired()){
                obsoleteKeys.add(key);
            }
        }
        SESSIONS_BY_COOKIE.keySet().removeAll(obsoleteKeys);
    }

    private static String generateUniqueSessionId() {
        List<String> existingSessionIds = new ArrayList<>();
        for(CookieSessionKey key : SESSIONS_BY_COOKIE.keySet()){
            existingSessionIds.add(key.cookieId());
        }
        return Tokens.generateUniqueToken(existingSessionIds);
    }
}
