package jewas.routes.security.josso;

import jewas.http.HttpRequest;
import jewas.http.session.Sessions;
import org.josso.agent.LocalSession;
import org.josso.agent.LocalSessionEvent;
import org.josso.agent.LocalSessionListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author fcamblor
 */
public class LocalSessionImpl implements LocalSession {

    /**
     * The session event listeners for this Session.
     */
    private transient ArrayList _listeners = new ArrayList();
    private long _creationTime;


    private String _id;

    private long _lastAccessedTime;

    private int _maxInactiveInterval;

    private String _sessionId;

    public LocalSessionImpl(HttpRequest request) {
        this._sessionId = Sessions.getId(request);
    }

    public long getCreationTime() {
        return _creationTime;
    }

    public String getId() {
        return _id;
    }

    public long getLastAccessedTime() {
        return _lastAccessedTime;
    }

    public void setMaxInactiveInterval(int i) {
        _maxInactiveInterval = i;
    }

    public int getMaxInactiveInterval() {
        return _maxInactiveInterval;
    }

    public void expire() {

        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            LocalSessionListener listener = (LocalSessionListener) i.next();

            listener.localSessionEvent(new LocalSessionEvent(this, LocalSession.LOCAL_SESSION_DESTROYED_EVENT, null));
        }
    }

    public void addSessionListener(LocalSessionListener sessionListener) {
        _listeners.add(sessionListener);
    }

    public void removeSessionListener(LocalSessionListener sessionListener) {
        _listeners.remove(sessionListener);
    }

    public void invalidate() {
        Sessions.invalidate(_sessionId);
    }

    @Override
    public Object getWrapped() {
        return _sessionId;
    }
}