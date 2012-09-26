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
    private static final ConcurrentMap<CookieSessionKey, HttpSession> SESSIONS_BY_COOKIE =
            new ConcurrentHashMap<>();

    private static final String SESSION_ID_COOKIE_KEY = "sessionId";

    /**
     * Will retrieve session map for given request
     */
    public static HttpSession get(HttpRequest request){
        // On every get, we should ensure there isn't any obsolete key in static session map
        cleanObsoleteSessions();

        HttpSession session = null;

        // Is there a session id sent in the request ?
        // If this is the case, we should try to retrieve corresponding session map
        // The retrieved session map could be null if, for instance, it was previously cleaned
        String cookieSessionId = getId(request);
        if(cookieSessionId != null){
            session = SESSIONS_BY_COOKIE.get(new CookieSessionKey(cookieSessionId));
        }

        // If session is not yet present, this is the first time we make a request on the site
        // => we should create the session cookie and initialize the session map
        if(session == null){
            // Initializing session
            // Note that there could be a race condition here where the same user could make 2 parallel "starting" queries
            // and thus, we could reach this block where session id is not yet created
            // then, 2 session ids would be generated for the same user, and the latest written cookie will correspond
            // to the *good* unerlying map.
            //
            // Problem is, here, that without any sessionId being set yet, we can't identify 2 different requests being sent
            // by the same client. So we cannot even add a synchronized block allowing to stop 2 different requests from the same host
            // But we can consider it wil likely be *very* rare to be in such a case because, generally, we always have
            // 1 request which loads the page (and then, generate sessionId), then N calls (for assets loading)
            String sessionId = generateUniqueSessionId();
            session = new HttpSession(sessionId);
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

    public static void invalidate(String sessionId) {
        SESSIONS_BY_COOKIE.remove(new CookieSessionKey(sessionId));
    }

    public static String getId(HttpRequest request) {
        Cookie cookie = request.cookie(SESSION_ID_COOKIE_KEY);
        return cookie==null?null:cookie.getValue();
    }
}
