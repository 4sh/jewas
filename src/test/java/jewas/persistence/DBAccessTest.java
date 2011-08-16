package jewas.persistence;

import jewas.persistence.util.JDBCUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
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

        Statement st = dbInitializationConnection.createStatement();
        st.execute("CREATE SEQUENCE TEST_SEQ INCREMENT BY 1 START WITH 1000");
        st.executeUpdate("CREATE TABLE test (id INTEGER, name char(50), last_name char(50), age INTEGER)");
        st.executeUpdate("INSERT INTO test VALUES (1, 'foo', 'FOO', 69)");
        st.executeUpdate("INSERT INTO test VALUES (2, 'bar', 'BAR', 10)");
        st.executeUpdate("INSERT INTO test VALUES (3, 'zot', 'ZOT', 32)");

        JDBCUtils.closeStatementIfNecessary(st);
    }

    @AfterClass
    public static void stop() throws SQLException {
        Statement sst = dbInitializationConnection.createStatement();

        sst.executeUpdate("DROP TABLE test");
        sst.executeUpdate("DROP SEQUENCE TEST_SEQ");

        JDBCUtils.closeStatementIfNecessary(sst);
        JDBCUtils.closeConnectionIfNecessary(dbInitializationConnection);
    }

    public static class TestEntry {
        private long id;
        private String name;
        private String lastName;
        private int age;

        public long id() {
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
        template.selectObjectsAndFill(allEntries, "select * from test", new QueryExecutionContext());
        assertThat(allEntries.size(), is(equalTo(3)));
    }

    @Test
    public void shouldPerformASelectWith1RowCorrectly() {
        QueryTemplate<TestEntry> template = createQueryTemplate();

        TestEntry entry = template.selectObject("select * from test where id=2", new QueryExecutionContext());
        assertThat(entry.id(), is(equalTo(Long.valueOf(2))));
        assertThat(entry.age(), is(equalTo(10)));
        assertThat(entry.name(), is(equalTo("bar")));
        assertThat(entry.lastName(), is(equalTo("BAR")));
    }

    @Test
    public void shouldPerformASelectWithMultipleQueryParameters() {
        QueryTemplate<TestEntry> template = createQueryTemplate();

        List<TestEntry> entries = new ArrayList<TestEntry>();
        template.selectObjectsAndFill(entries, "select id, name, last_name, age from test where id > :minId and name in :nameWhiteList",
                new QueryExecutionContext().buildParams()
                        .integer("minId", 0)
                        .<String>array("nameWhiteList", "bar", "azerty")
                .toContext()
        );

        assertThat(entries.size(), is(equalTo(1)));
        assertThat(entries.get(0).id(), is(equalTo(Long.valueOf(2))));
    }

    @Ignore("Not yet implemented ...")
    @Test
    public void shouldPerformFilledOptionalQueryParameter() {
        QueryTemplate<TestEntry> template = createQueryTemplate();

        List<TestEntry> entries = new ArrayList<TestEntry>();
        template.selectObjectsAndFill(entries, "select id, name, last_name, age from test where id > 0 [? and id in :idWhiteList]",
                new QueryExecutionContext().buildParams()
                        .<Integer>array("idWhiteList", Integer.valueOf(2), Integer.valueOf(4))
                .toContext()
        );

        assertThat(entries.size(), is(equalTo(1)));
        assertThat(entries.get(0).id(), is(equalTo(Long.valueOf(2))));
    }

    @Ignore("Not yet implemented ...")
    @Test
    public void shouldPerformNotFilledOptionalQueryParameter() {
        QueryTemplate<TestEntry> template = createQueryTemplate();

        List<TestEntry> entries = new ArrayList<TestEntry>();
        template.selectObjectsAndFill(entries, "select id, name, last_name, age from test where id > 0 [? and id in :idWhiteList]",
                new QueryExecutionContext()
                // Here, we don't fill the idWhiteList and expect the "and id in ..." clause won't be added to the request
        );

        assertThat(entries.size(), is(equalTo(3)));
        assertThat(entries.get(0).id(), is(equalTo(Long.valueOf(1))));
        assertThat(entries.get(1).id(), is(equalTo(Long.valueOf(2))));
        assertThat(entries.get(2).id(), is(equalTo(Long.valueOf(3))));
    }

    @Test
    public void shouldCreateUpdateReadAndDeleteRecordBeOk(){
        QueryTemplate<TestEntry> template = createQueryTemplate();

        // I would have prefered to not use nextval here but heh.. didn't succceed :(
        Map<String,String> genKeys = template.insert("insert into test (id, name, last_name, age) values (TEST_SEQ.NEXTVAL, :name, :last_name, :age)",
                new QueryExecutionContext().buildParams()
                        .string("name", "toto")
                        .string("last_name", "tutu")
                        .integer("age", 20)
                .toContext(), "id");
        assertThat(genKeys.size(), is(equalTo(1)));
        Long generatedId = Long.valueOf(genKeys.get("id"));
        assertThat(generatedId, is(greaterThanOrEqualTo(Long.valueOf(1000))));


        TestEntry entry = template.selectObject("select id, name, last_name, age from test where id = :id",
                new QueryExecutionContext().buildParams().bigint("id", generatedId).toContext());
        assertThat(entry, is(notNullValue()));
        assertThat(entry.id(), is(equalTo(generatedId)));
        assertThat(entry.name(), is(equalTo("toto")));
        assertThat(entry.lastName(), is(equalTo("tutu")));
        assertThat(entry.age(), is(equalTo(20)));


        int rowsUpdated = template.update("UPDATE test SET name = :newName WHERE id = :id",
                new QueryExecutionContext().buildParams()
                        .string("newName", "newToto")
                        .bigint("id", generatedId)
                .toContext());
        assertThat(rowsUpdated, is(equalTo(1)));


        entry = template.selectObject("select id, name, last_name, age from test where id = :id",
                new QueryExecutionContext().buildParams().bigint("id", generatedId).toContext());
        assertThat(entry, is(notNullValue()));
        assertThat(entry.id(), is(equalTo(generatedId)));
        assertThat(entry.name(), is(equalTo("newToto")));
        assertThat(entry.lastName(), is(equalTo("tutu")));
        assertThat(entry.age(), is(equalTo(20)));


        rowsUpdated = template.delete("delete test where id = :id",
                new QueryExecutionContext().buildParams().bigint("id", generatedId).toContext());
        assertThat(rowsUpdated, is(equalTo(1)));

        
        entry = template.selectObject("select id, name, last_name, age from test where id = :id",
                new QueryExecutionContext().buildParams().bigint("id", generatedId).toContext());
        assertThat(entry, is(nullValue()));
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
