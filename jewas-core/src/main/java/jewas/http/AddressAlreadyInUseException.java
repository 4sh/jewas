package jewas.http;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/17/11
 * Time: 12:24 AM
 */
public class AddressAlreadyInUseException extends RuntimeException {

    public AddressAlreadyInUseException(String application, int portNumber, Throwable cause){
        super(String.format("Application <%s> can't start on port <%s>.%n" +
                "Another instance/service is probably running using the same port.",
                application, portNumber),
              cause);
    }
}
