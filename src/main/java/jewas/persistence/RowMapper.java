package jewas.persistence;

import java.sql.ResultSet;

/**
 * @author fcamblor
 */
public interface RowMapper<T> {
    public T processRow(ResultSet rs);
}
