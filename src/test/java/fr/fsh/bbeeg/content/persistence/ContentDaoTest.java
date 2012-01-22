package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.content.persistence.mocks.DomainDaoMock;
import fr.fsh.bbeeg.content.persistence.mocks.ElasticSearchDaoMock;
import fr.fsh.bbeeg.content.persistence.mocks.TagDaoMock;
import fr.fsh.bbeeg.content.persistence.mocks.UserDaoMock;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.user.pojos.User;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ContentDaoTest {

    private static final String JDBC_URL = "jdbc:h2:mem:test";
    private static final String DRIVER_CLASS = "org.h2.Driver";
    private IDatabaseTester databaseTester;
    private BasicDataSource dataSource;
    private ContentDao contentDao;
    private String expectedDataSetsPath = "/expectedDataSet/";


    @Before
    public void setUp() throws Exception
    {
        databaseTester = new JdbcDatabaseTester(DRIVER_CLASS,
            JDBC_URL, "sa", "sa");

        // Initialize dataSource
        dataSource = new BasicDataSource();
        dataSource.setUrl(JDBC_URL);
        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

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

    protected void tearDown() throws Exception
    {
	// will call default tearDownOperation
        databaseTester.onTearDown();
    }

    @Test
    public void createContentTest() throws Exception {
        // Initialize ContentDao
        contentDao = new ContentDao(dataSource, null, new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Setup
        Date today = new DateMidnight().toDate();

        String description = "description content";
        String title = "Content title";
        ContentHeader contentHeader = new ContentHeader()
                .status(null)
                .ancestorId(null)
                .author(new User().id(1000L))
                .creationDate(null)
                .description(description)
                .domains(null)
                .endPublicationDate(null)
                .id(null)
                .lastModificationDate(null)
                .startPublicationDate(null)
                .popularity(0L)
                .tags(null)
                .title(title)
                .type(ContentType.TEXT)
                .version(0);

        ContentDetail creation = new ContentDetail();
        creation.header(contentHeader)
                .publicationComments(null)
                .rejectionComments(null)
                .url(null);

        contentDao.createContent(creation);

        ContentDetail newVersion = new ContentDetail();
        newVersion.header(contentHeader)
                .publicationComments(null)
                .rejectionComments(null)
                .url(null)
                .header().ancestorId(1000L).version(1);

        contentDao.createContent(newVersion);

        // Fetch database data after executing your code
        IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable("CONTENT");

        // Load expected data from an XML dataset
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(expectedDataSetsPath + "expectedCreateContentTestDataSet.xml"));
        ITable expectedTable = expectedDataSet.getTable("CONTENT");

        // Assert actual database table match expected table
        assertThat(expectedTable.getRowCount(), is(equalTo(actualTable.getRowCount())));
        assertThat(expectedTable.getValue(0, "ID").toString(), is(equalTo(actualTable.getValue(0, "ID").toString())));
        assertThat(expectedTable.getValue(1, "ID").toString(), is(equalTo(actualTable.getValue(1, "ID").toString())));

        assertThat(expectedTable.getValue(0, "TITLE"), is(equalTo(actualTable.getValue(0, "TITLE"))));
        assertThat(expectedTable.getValue(1, "TITLE"), is(equalTo(actualTable.getValue(1, "TITLE"))));

        assertThat(expectedTable.getValue(0, "DESCRIPTION"), is(equalTo(actualTable.getValue(0, "DESCRIPTION"))));
        assertThat(expectedTable.getValue(1, "DESCRIPTION"), is(equalTo(actualTable.getValue(1, "DESCRIPTION"))));

        assertThat(String.valueOf(ContentStatus.DRAFT.ordinal()), is(equalTo(actualTable.getValue(0, "STATUS").toString())));
        assertThat(String.valueOf(ContentStatus.DRAFT.ordinal()), is(equalTo(actualTable.getValue(1, "STATUS").toString())));

        assertThat(expectedTable.getValue(0, "VERSION").toString(), is(equalTo(actualTable.getValue(0, "VERSION").toString())));
        assertThat(expectedTable.getValue(1, "VERSION").toString(), is(equalTo(actualTable.getValue(1, "VERSION").toString())));

        assertThat(expectedTable.getValue(0, "CONTENT_ANCESTOR_REF").toString(), is(equalTo(actualTable.getValue(0, "CONTENT_ANCESTOR_REF").toString())));
        assertThat(expectedTable.getValue(1, "CONTENT_ANCESTOR_REF").toString(), is(equalTo(actualTable.getValue(1, "CONTENT_ANCESTOR_REF").toString())));



// status is DRAFT
// titlte
        //description
        // creation Date
        //
    }
}
