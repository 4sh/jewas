package jewas.persistence.exception;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/22/11
 * Time: 3:56 PM
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
