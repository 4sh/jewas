package jewas.persistence.exception;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/22/11
 * Time: 4:05 PM
 */
public class CannotGetJDBCConnectionException extends DataAccessException {
    public CannotGetJDBCConnectionException(String message, SQLException e) {
        super(message, e);
    }
}
