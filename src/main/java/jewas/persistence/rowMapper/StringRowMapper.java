package jewas.persistence.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author driccio
 */
public class StringRowMapper implements RowMapper<String> {
    @Override
    public String processRow(ResultSet rs) throws SQLException {
        return rs.getString(1);
    }
}
