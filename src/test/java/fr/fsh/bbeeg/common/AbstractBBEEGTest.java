package fr.fsh.bbeeg.common;

import fr.fsh.bbeeg.content.persistence.DatabaseInitializator;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractBBEEGTest {

    /**
     * Tests database JDBC URL.
     */
    private static final String JDBC_URL = "jdbc:h2:mem:bbeeg;MODE=MySQL";

    private static final String USERNAME = "bbeeg";

    private static final String PASSWORD = "bbeeg";

    /**
     * Database driver class.
     */
    private static final String DRIVER_CLASS = "org.h2.Driver";

    /**
     * The database tester interface.
     */
    private static IDatabaseTester databaseTester;

    /**
     * The test Datasource.
     */
    private static BasicDataSource dataSource;

    /**
     * The path to init data files.
     */
    private static String initDataSetFilesPath = "/initDataSet/";
    
    /**
     * Method executed before each test of class.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        databaseTester = new JdbcDatabaseTester(DRIVER_CLASS,
                JDBC_URL, USERNAME, PASSWORD);

        // Initialize dataSource
        dataSource = new BasicDataSource();
        dataSource.setUrl(JDBC_URL);
        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setDefaultAutoCommit(true);
        // Call DB maintain here to create in memory database schema
        //URL configurationUrl = new File("dbmaintain.properties").toURI().toURL();
        //MainFactory mainFactory = new MainFactory(configurationUrl);
        //DbMaintainer dbMaintainer = createDbMaintainer();
        //dbMaintainer.updateDatabase(false);
        DatabaseInitializator.initDB(JDBC_URL);

        // initialize your dataset here
        //IDataSet dataSet = null;
        //new FlatXmlDataSetBuilder().build(new FileInputStream("dataset.dtd"));

        //databaseTester.setDataSet( dataSet );
        // will call default setUpOperation
        //databaseTester.onSetup();
    }

    /**
     * Method executed after each tests of class.
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        // will call default tearDownOperation
        databaseTester.onTearDown();
    }

    public IDatabaseTester databaseTester(){
        return this.databaseTester;
    }

    public BasicDataSource dataSource(){
        return this.dataSource;
    }

    public String initDataSetFilesPath() {
        return this.initDataSetFilesPath;
    }
    
}