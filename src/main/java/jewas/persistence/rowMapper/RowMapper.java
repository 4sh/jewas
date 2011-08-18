package jewas.persistence.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author fcamblor
 */
public interface RowMapper<T> {
    public T processRow(ResultSet rs) throws SQLException;
}
