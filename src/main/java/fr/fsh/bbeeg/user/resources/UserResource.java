package fr.fsh.bbeeg.user.resources;

import fr.fsh.bbeeg.common.resources.DateResultObject;
import org.joda.time.DateMidnight;

/**
 * @author driccio
 */
public class UserResource {
    public static DateResultObject getLastConnectionDate() {
        return new DateResultObject(new DateMidnight());
    }
}
