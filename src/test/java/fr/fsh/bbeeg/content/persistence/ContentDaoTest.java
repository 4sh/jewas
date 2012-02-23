package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.common.AbstractBBEEGTest;
import fr.fsh.bbeeg.content.persistence.mocks.DomainDaoMock;
import fr.fsh.bbeeg.content.persistence.mocks.ElasticSearchDaoMock;
import fr.fsh.bbeeg.content.persistence.mocks.TagDaoMock;
import fr.fsh.bbeeg.content.persistence.mocks.UserDaoMock;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.user.pojos.User;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.elasticsearch.common.joda.time.DateMidnight;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ContentDaoTest extends AbstractBBEEGTest {

    private ContentDao contentDao;
    private String expectedDataSetsPath = "/expectedDataSet/";
    private String daoDataSetPath = "contentDaoTest/";
    @Test
    public void createContentTest() throws Exception {
        // Initialize ContentDao
        contentDao = new ContentDao(dataSource(), null, new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Setup
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
        IDataSet databaseDataSet = databaseTester().getConnection().createDataSet();
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
    }

    @Test
    public void archivePreviousVersionTest() {
        fail("To implement");
    }

    @Test
    public void getContentDetailTest() {
        fail("To implement");
    }

    @Test
    public void getHigherVersionNumber() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), null, new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "getHigherVersionNumber.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }

        ITable actualTable = dataSet.getTable("CONTENT");

        Long ancestorId = 156L;
        Long hierarchyMiddleId = 243L;
        Long childId = 267L;
        Long anotherId = 666L;

        /* Check initial row count */
        assertThat(4, is(equalTo(actualTable.getRowCount())));
        /* Check initial rows ids */
        assertThat(ancestorId.toString(), is(equalTo(actualTable.getValue(0, "ID").toString())));
        assertThat(hierarchyMiddleId.toString(), is(equalTo(actualTable.getValue(1, "ID").toString())));
        assertThat(childId.toString(), is(equalTo(actualTable.getValue(2, "ID").toString())));
        assertThat(anotherId.toString(), is(equalTo(actualTable.getValue(3, "ID").toString())));
        /* Check initial rows CONTENT_ANCESTOR_REF column values */
        assertThat(ancestorId.toString(), is(equalTo(actualTable.getValue(0, "CONTENT_ANCESTOR_REF"))));
        assertThat(ancestorId.toString(), is(equalTo(actualTable.getValue(1, "CONTENT_ANCESTOR_REF"))));
        assertThat(ancestorId.toString(), is(equalTo(actualTable.getValue(2, "CONTENT_ANCESTOR_REF"))));
        assertThat(ancestorId.toString(), is(not(equalTo(actualTable.getValue(3, "CONTENT_ANCESTOR_REF")))));
        /* Check initial rows VERSION column */
        assertThat("0", is(equalTo(actualTable.getValue(0, "VERSION"))));
        assertThat("1", is(equalTo(actualTable.getValue(1, "VERSION"))));
        assertThat("2", is(equalTo(actualTable.getValue(2, "VERSION"))));
        assertThat("123", is(equalTo(actualTable.getValue(3, "VERSION"))));
        
        int higherVersionNumber = contentDao.getHigherVersionNumber(ancestorId);

        assertThat(2, is(equalTo(higherVersionNumber)));
    }

    @Test
    public void incrementPopularity() {
        fail("To implement");
    }

    @Test
    public void fetchRecentContentsTest() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), null, new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "fetchRecentContents.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }

        ITable actualTable = dataSet.getTable("CONTENT");

        Date today = new DateMidnight().toDate();

        /* Check initial row count */
        assertThat(actualTable.getRowCount(), is(equalTo(5)));

        /* Check there are 3 contents eligible to be retrieved by the query */
        assertThat(actualTable.getValue(0, "STATUS").toString(), is(equalTo("2")));
        assertThat(actualTable.getValue(1, "STATUS").toString(), is(equalTo("2")));
        assertThat(actualTable.getValue(2, "STATUS").toString(), is(equalTo("2")));

        assertThat(actualTable.getValue(0, "PUBLICATION_START_DATE").toString(), is(equalTo("1011-12-13")));
        assertThat(actualTable.getValue(0, "PUBLICATION_END_DATE").toString(), is(equalTo("3011-12-13")));
        assertThat(actualTable.getValue(1, "PUBLICATION_START_DATE"), is(nullValue()));
        assertThat(actualTable.getValue(1, "PUBLICATION_END_DATE"), is(nullValue()));
        assertThat(actualTable.getValue(2, "PUBLICATION_START_DATE"), is(nullValue()));
        assertThat(actualTable.getValue(2, "PUBLICATION_END_DATE"), is(nullValue()));

        /* Check that one content has publication dates which do not include today */
        Date startPublication = new DateMidnight((String) actualTable.getValue(4, "PUBLICATION_START_DATE")).toDate();
        Date endPublication = new DateMidnight((String) actualTable.getValue(4, "PUBLICATION_END_DATE")).toDate();
        assertThat(today, not(allOf(is(greaterThanOrEqualTo(startPublication)), is(lessThanOrEqualTo(endPublication)))));
        /* Check that one content has not the VALIDATED status */
        assertThat("2", is(not(equalTo(actualTable.getValue(3, "STATUS")))));

        List<ContentHeader> contentHeaders = new ArrayList<ContentHeader>();
        contentDao.fetchRecentContents(contentHeaders, 2);

        /* Check the limit has been respected */
        assertThat(contentHeaders.size(), is(equalTo(2)));
        /* Check the order of the returned contents */
        assertThat(contentHeaders.get(0).id(), is(equalTo(102L)));
        assertThat(contentHeaders.get(1).id(), is(equalTo(101L)));
    }

    @Test
    public void fetchPopularContents() {
        fail("To implement");
    }
}
