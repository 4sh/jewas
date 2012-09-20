package jewas.persistence.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author fcamblor
 */
public class JDBCUtils {

    /**
     * Try to close the connection and, if any error happens, don't complain
     *
     * @param con The connection to close
     */
    public static void closeConnectionIfNecessary(Connection con) {
        try {
            closeConnection(con);
        } catch (SQLException ex) {
            // Don't do anything here... if connection is already closed, we want to filter
            // exception
            // TODO: add a log when logs will be settled up ?
        }
    }

    public static void closeConnection(Connection con) throws SQLException {
        if (con == null) {
            return;
        }
        con.close();
    }

    // Yeah it is copy/pasted code here ... missing Closable interface from jdk 7 ..

    /**
     * Try to close the statement and, if any error happens, don't complain
     *
     * @param st The Statement to close
     */
    public static void closeStatementIfNecessary(Statement st) {
        try {
            closeStatement(st);
        } catch (SQLException ex) {
            // Don't do anything here... if statement is already closed, we want to filter
            // exception
            // TODO: add a log when logs will be settled up ?
        }
    }

    public static void closeStatement(Statement st) throws SQLException {
        if (st == null) {
            return;
        }
        st.close();
    }
}
