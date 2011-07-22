package fr.fsh.bbeeg.h2;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;


/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 22/07/11
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class H2UseIntegrationTest {
    private static Connection con;

    @BeforeClass
    public static void start() throws ClassNotFoundException, SQLException {
        // load the driver class
        Class.forName("org.h2.Driver");
        con = DriverManager.getConnection("jdbc:h2:mem:mytest", "sa", "");
        // here you create the table
        String s = "CREATE TABLE test (id INTEGER, name char(50), last_name char(50), age INTEGER)";
        Statement sst = con.createStatement();
        sst.executeUpdate(s);
        sst.executeUpdate("INSERT INTO test VALUES (1, 'foo', 'bar', 69)");
    }

    @AfterClass
    public static void stop() throws SQLException {
        String s = "DROP TABLE test";
        Statement sst = con.createStatement();
        sst.executeUpdate(s);
        con.close();
    }

    @Test
    public void getInfoFromDatabaseTest() throws SQLException {
        String s = "SELECT * FROM test t WHERE t.id = 1";
        Statement sst = con.createStatement();
        ResultSet rs = sst.executeQuery(s);

        rs.last();
        Assert.assertThat(rs.getRow(), is(equalTo(1)));

        Integer id = rs.getInt(1);
        Assert.assertThat(id, is(equalTo(1)));

        String name = rs.getString(2);
        Assert.assertThat(name, is(equalTo("foo")));

        String last_name = rs.getString(3);
        Assert.assertThat(last_name, is(equalTo("bar")));

        Integer age = rs.getInt(4);
        Assert.assertThat(age, is(equalTo(69)));
    }
}
