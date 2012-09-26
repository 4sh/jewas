package jewas.http.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fcamblor
 */
public class HttpSession {
    private String sessionId;
    private ConcurrentMap<String, Serializable> attributes;

    public HttpSession(String sessionId){
        this.sessionId = sessionId;
        this.attributes = new ConcurrentHashMap<>();
    }

    public String id(){
        return sessionId;
    }

    public Serializable get(String name){
        return attributes.get(name);
    }

    public void set(String name, Serializable value){
        attributes.put(name, value);
    }

    public Serializable remove(String name){
        return attributes.remove(name);
    }

    public void invalidate(){
        Sessions.invalidate(sessionId);
    }
}
