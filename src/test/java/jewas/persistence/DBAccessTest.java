package jewas.persistence;

import jewas.persistence.util.JDBCUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class DBAccessTest {
    private static Connection dbInitializationConnection;

    @BeforeClass
    public static void start() throws ClassNotFoundException, SQLException {
        // load the driver class
        Class.forName("org.h2.Driver");
        dbInitializationConnection = DriverManager.getConnection("jdbc:h2:mem:mytest", "sa", "");
        // here you create the table
        String s = "CREATE TABLE test (id INTEGER, name char(50), last_name char(50), age INTEGER)";
        Statement sst = dbInitializationConnection.createStatement();
        sst.executeUpdate(s);
        sst.executeUpdate("INSERT INTO test VALUES (1, 'foo', 'FOO', 69)");
        sst.executeUpdate("INSERT INTO test VALUES (2, 'bar', 'BAR', 10)");
        sst.executeUpdate("INSERT INTO test VALUES (3, 'zot', 'ZOT', 32)");
        JDBCUtils.closeStatementIfNecessary(sst);
    }

    @AfterClass
    public static void stop() throws SQLException {
        String s = "DROP TABLE test";
        Statement sst = dbInitializationConnection.createStatement();
        sst.executeUpdate(s);
        JDBCUtils.closeStatementIfNecessary(sst);
        JDBCUtils.closeConnectionIfNecessary(dbInitializationConnection);
    }

    public static class TestEntry {
        private int id;
        private String name;
        private String lastName;
        private int age;

        public int id() {
            return id;
        }

        public String name() {
            return name;
        }

        public String lastName() {
            return lastName;
        }

        public int age() {
            return age;
        }

        public TestEntry id(int id) {
            this.id = id;
            return this;
        }

        public TestEntry name(String name) {
            this.name = name;
            return this;
        }

        public TestEntry lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public TestEntry age(int age) {
            this.age = age;
            return this;
        }
    }

    @Test
    public void shouldPerformASelectWithNRowsCorrectly() {
        QueryTemplate<TestEntry> template = createQueryTemplate();

        List<TestEntry> allEntries = new ArrayList<TestEntry>();
        template.selectObjectsAndFill(allEntries, "select * from test", new QueryContext());
        assertThat(allEntries.size(), is(equalTo(3)));
    }

    @Test
    public void shouldPerformASelectWith1RowCorrectly() {
        QueryTemplate<TestEntry> template = createQueryTemplate();

        TestEntry entry = template.selectObject("select * from test where id=2", new QueryContext());
        assertThat(entry.id(), is(equalTo(2)));
        assertThat(entry.age(), is(equalTo(10)));
        assertThat(entry.name(), is(equalTo("bar")));
        assertThat(entry.lastName(), is(equalTo("BAR")));
    }

    private static QueryTemplate<TestEntry> createQueryTemplate() {
        return new QueryTemplate<TestEntry>(createDatasource(), new RowMapper<TestEntry>() {
            @Override
            public TestEntry processRow(ResultSet rs) throws SQLException {
                return new TestEntry()
                        .id(rs.getInt("id")).age(rs.getInt("age"))
                        .name(rs.getString("name")).lastName(rs.getString("last_name"));
            }
        });

    }

    private static DataSource createDatasource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:h2:mem:mytest");
        ds.setDriverClassName("org.h2.Driver");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }
}
