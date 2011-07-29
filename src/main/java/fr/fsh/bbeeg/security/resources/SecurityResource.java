package fr.fsh.bbeeg.security.resources;

/**
 * @author driccio
 */
public class SecurityResource {
    public static Boolean connectUser(ConnectionInformation info) {
        // TODO: implement it

        if ("bbeeg".equals(info.login()) && "test".equals(info.password())) {
            return true;
        } else {
            return false;
        }
    }
}
