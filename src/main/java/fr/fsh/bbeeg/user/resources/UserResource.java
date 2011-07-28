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

    public static User getUserInformations() {
        User user = new User();
        user.name("Bob").surname("Sponge").email("pacific_33@sea.gl")
                .lastConnectionDate(new DateMidnight());
        return user;
    }
}
