package jewas.http.session;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fcamblor
 */
public class CookieSessionKey implements Serializable {
    /**
     * Session should expire after 15 minutes
     */
    private static final long SESSION_EXPIRATION_TIMEOUT = 1000 * 60 * 15;

    String cookieId;
    Date expiresOn;

    public CookieSessionKey(String cookieId){
        this.cookieId = cookieId;
        updateExpiration();
    }

    public void updateExpiration() {
        this.expiresOn = new Date(new Date().getTime() + SESSION_EXPIRATION_TIMEOUT);
    }

    protected String cookieId(){
        return this.cookieId;
    }

    public boolean expired(){
        return new Date().after(this.expiresOn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CookieSessionKey that = (CookieSessionKey) o;

        if (cookieId != null ? !cookieId.equals(that.cookieId) : that.cookieId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return cookieId != null ? cookieId.hashCode() : 0;
    }
}
