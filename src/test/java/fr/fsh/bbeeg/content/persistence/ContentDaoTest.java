package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.common.AbstractBBEEGTest;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.content.persistence.mocks.*;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.pojos.User;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.elasticsearch.common.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ContentDaoTest extends AbstractBBEEGTest {

    private ContentDao contentDao;
    private String expectedDataSetsPath = "/expectedDataSet/";
    private String daoDataSetPath = "contentDaoTest/";
    @Test
    public void createContentTest() throws Exception {
        // Initialize ContentDao
        contentDao = new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        String[] tables =  {"CONTENT"};
        IDataSet databaseDataSet = databaseTester().getConnection().createDataSet(tables);
        try {
            DatabaseOperation.DELETE_ALL.execute(databaseTester().getConnection(), databaseDataSet);
        } finally {
            databaseTester().getConnection().close();
        }

        // Setup
        String description = "description content";
        String title = "Content title";
        ContentHeader contentHeader = new ContentHeader()
                .status(null)
                .ancestorId(null)
                .author(new User().id(1L))
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
                .header().ancestorId(1L).version(1);

        contentDao.createContent(newVersion);

        // Fetch database data after executing your code
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
    public void archivePreviousVersionTest() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "archivePreviousVersion.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }

        ITable actualTable = dataSet.getTable("CONTENT");
        Long commonAncestorId = 0L;

        /* Check initial row count */
        assertThat(3, is(equalTo(actualTable.getRowCount())));
        /* Check initial rows STATUS column */
        assertThat("6", is(equalTo(actualTable.getValue(0, "STATUS"))));
        assertThat("2", is(equalTo(actualTable.getValue(1, "STATUS"))));
        assertThat("0", is(equalTo(actualTable.getValue(2, "STATUS"))));
        /* Check initial rows CONTENT_ANCESTOR_REF column */
        assertThat(commonAncestorId + "", is(equalTo(actualTable.getValue(0, "CONTENT_ANCESTOR_REF"))));
        assertThat(commonAncestorId + "", is(equalTo(actualTable.getValue(1, "CONTENT_ANCESTOR_REF"))));
        assertThat(commonAncestorId + "", is(equalTo(actualTable.getValue(2, "CONTENT_ANCESTOR_REF"))));

        /* Check initial rows CONTENT_ANCESTOR_REF column */
        assertThat(0 + "", is(equalTo(actualTable.getValue(0, "VERSION"))));
        assertThat(1 + "", is(equalTo(actualTable.getValue(1, "VERSION"))));
        assertThat(2 + "", is(equalTo(actualTable.getValue(2, "VERSION"))));

        this.contentDao.archivePreviousVersion(commonAncestorId);

        IDataSet afterExecutionDataSet = databaseTester().getConnection().createDataSet();
        ITable contentTable = afterExecutionDataSet.getTable("CONTENT");

        assertThat(contentTable.getValue(0, "STATUS").toString(), is(equalTo(6 + "")));
        assertThat(contentTable.getValue(1, "STATUS").toString(), is(equalTo(6 + "")));
        assertThat(contentTable.getValue(2, "STATUS").toString(), is(equalTo(0 + "")));
    }

   @Test
    public void updateContentTest() throws Exception {
        UserDao userDao = new UserDao(dataSource(), new DomainDaoMock());
        this.contentDao = new ContentDao(dataSource(), userDao, new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "updateContent.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }
        ITable actualContentTable = dataSet.getTable("CONTENT");
        ITable actualContentCommentTable = dataSet.getTable("CONTENT_COMMENT");
        ITable actualContentDomainTable = dataSet.getTable("CONTENT_DOMAIN");

        Long contentId = 1L;
        int contentVersion = 0;
        String contentTitle = "TITLE";
        String contentDescription = "DESCRIPTION";
        ContentStatus contentStatus = ContentStatus.ARCHIVED;

        /* Check initial row count */
        assertThat(actualContentTable.getRowCount(), is(equalTo(1)));
        assertThat(actualContentCommentTable.getRowCount(), is(equalTo(1)));

        ContentDetail contentDetail = new ContentDetail();
        contentDetail.header(new ContentHeader());
        contentDetail.header().id(contentId);
        contentDetail.header().status(contentStatus);
        contentDetail.header().title(contentTitle);
        contentDetail.header().description(contentDescription);
        contentDetail.header().version(contentVersion);
        /* Keep tag1 and tag3, remove tag2 and add tag4 */
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag3");
        tags.add("tag4");
        contentDetail.header().tags(tags);

        /* Keep domain 1, remove domain 2 and add domain 3 */
        List<Domain> domains = new ArrayList<>();
        domains.add(new Domain().id(1L));
        domains.add(new Domain().id(3L));
        contentDetail.header().domains(domains);

        contentDao.updateContent(contentDetail);

        IDataSet afterExecutionDataSet = databaseTester().getConnection().createDataSet();
        ITable contentTable = afterExecutionDataSet.getTable("CONTENT");

        assertThat(contentTable.getValue(0, "ID").toString(), is(equalTo(contentId + "")));
        assertThat(contentTable.getValue(0, "TITLE").toString(), is(equalTo(contentTitle)));
        assertThat(contentTable.getValue(0, "DESCRIPTION").toString(), is(equalTo(contentDescription)));
        assertThat(contentTable.getValue(0, "VERSION").toString(), is(equalTo(contentVersion + "")));

        /* Clean up database */
        try {
            DatabaseOperation.DELETE_ALL.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }
    }

    @Test
    public void updateLastConsultationDateTest() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "updateLastConsultationDate.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }

        ITable actualTable = dataSet.getTable("CONTENT");
        Long contentId = 0L;

        /* Check initial row count */
        assertThat(1, is(equalTo(actualTable.getRowCount())));
        /* Check initial rows LAST_CONSULTATION_DATE column */
        assertThat(actualTable.getValue(0, "LAST_CONSULTATION_DATE").toString(), is(equalTo("1212-12-12 12:12:12")));

        Date lastConsultationDate = new DateTime().toDate();
        this.contentDao.updateLastConsultationDate(contentId,lastConsultationDate );

        IDataSet afterExecutionDataSet = databaseTester().getConnection().createDataSet();
        ITable contentTable = afterExecutionDataSet.getTable("CONTENT");
        Date fromDB = new DateTime(contentTable.getValue(0, "LAST_CONSULTATION_DATE")).toDate();
        assertThat(fromDB, is(equalTo(lastConsultationDate)));
    }

   /* @Test
    public void updateContentPublicationCommentsTest() {
        fail("To implement");
    }*/

    /*@Test
    public void updateContentPublicationDatesTest() {
        fail("To implement");
    }*/

    @Test
    public void updateContentStatusTest() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "updateContentStatus.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }

        ITable actualTable = dataSet.getTable("CONTENT");
        Long contentId = 0L;

        /* Check initial row count */
        assertThat(1, is(equalTo(actualTable.getRowCount())));
        /* Check initial rows STATUS column */
        assertThat(actualTable.getValue(0, "STATUS").toString(), is(equalTo("0")));

        this.contentDao.updateContentStatus(contentId, ContentStatus.REJECTED);

        IDataSet afterExecutionDataSet = databaseTester().getConnection().createDataSet();
        ITable contentTable = afterExecutionDataSet.getTable("CONTENT");
        assertThat(contentTable.getValue(0, "STATUS").toString(), is(equalTo(ContentStatus.REJECTED.ordinal() + "")));
    }

    /*@Test
    public void selectAll() {
        fail("To implement");
    }*/

    @Test
    public void getTotalNumberOfContentsTest() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "getTotalNumberOfContents.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }
        ITable actualTable = dataSet.getTable("CONTENT");
        /* Check initial row count */
        assertThat(5, is(equalTo(actualTable.getRowCount())));

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
       
        Count totalNumberOfContent = this.contentDao.getTotalNumberOfContent();
        assertThat(3, is(equalTo(totalNumberOfContent.count())));
    }


    @Test
    public void getHigherVersionNumber() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

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
    public void incrementPopularity() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "incrementPopularity.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }
        ITable actualTable = dataSet.getTable("CONTENT");
        /* Check initial row count */
        assertThat(actualTable.getRowCount(), is(equalTo(1)));

        /* Check the content POPULARITY column values */
        assertThat(actualTable.getValue(0, "POPULARITY").toString(), is(equalTo("100")));
        this.contentDao.incrementPopularity(0L);

        IDataSet afterExecutionDataSet = databaseTester().getConnection().createDataSet();
        ITable contentTable = afterExecutionDataSet.getTable("CONTENT");
        // Assert actual database table match expected table
        assertThat(contentTable.getValue(0, "POPULARITY").toString(), is(equalTo("101")));
    }

    @Test
    public void fetchRecentContentsTest() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

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
    public void fetchPopularContents() throws Exception {
        this.contentDao =  new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "fetchPopularContents.xml"));
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

        /* Check the POPULARITY COLUMN of the 3 eligible contents */
        assertThat(actualTable.getValue(0, "POPULARITY").toString(), is(equalTo("100")));
        assertThat(actualTable.getValue(1, "POPULARITY").toString(), is(equalTo("90")));
        assertThat(actualTable.getValue(2, "POPULARITY").toString(), is(equalTo("110")));

        /* Check that one content has publication dates which do not include today */
        Date startPublication = new DateMidnight((String) actualTable.getValue(4, "PUBLICATION_START_DATE")).toDate();
        Date endPublication = new DateMidnight((String) actualTable.getValue(4, "PUBLICATION_END_DATE")).toDate();
        assertThat(today, not(allOf(is(greaterThanOrEqualTo(startPublication)), is(lessThanOrEqualTo(endPublication)))));

        /* Check that one content has not the VALIDATED status */
        assertThat("2", is(not(equalTo(actualTable.getValue(3, "STATUS")))));

        List<ContentHeader> contentHeaders = new ArrayList<ContentHeader>();
        contentDao.fetchPopularContent(contentHeaders, 2);

        /* Check the limit has been respected */
        assertThat(contentHeaders.size(), is(equalTo(2)));
        /* Check the order of the returned contents */
        assertThat(contentHeaders.get(0).id(), is(equalTo(103L)));
        assertThat(contentHeaders.get(1).id(), is(equalTo(101L)));
    }

    @Test
    public void fetchLastViewedContents() throws Exception {
        this.contentDao = new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "fetchLastViewedContents.xml"));
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
        contentDao.fetchLastViewedContent(contentHeaders, 2);

        /* Check the limit has been respected */
        assertThat(contentHeaders.size(), is(equalTo(2)));
        /* Check the order of the returned contents */
        assertThat(contentHeaders.get(0).id(), is(equalTo(102L)));
        assertThat(contentHeaders.get(1).id(), is(equalTo(101L)));
    }
    
    @Test
    public void getContentUrlTest() throws Exception {
        this.contentDao = new ContentDao(dataSource(), new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "getContentUrl.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }
        ITable actualTable = dataSet.getTable("CONTENT");

        Long contentId = 156L;
        String fileURi = "/content/156.test";

        /* Check initial row count */
        assertThat(actualTable.getRowCount(), is(equalTo(1)));

        /* Check content ID column value */
        assertThat(actualTable.getValue(0, "ID").toString(), is(equalTo(contentId.toString())));
        /* Check the content FILE_URI column value */
        assertThat(actualTable.getValue(0, "FILE_URI").toString(), is(equalTo(fileURi)));
        List<ContentHeader> contentHeaders = new ArrayList<ContentHeader>();
        String url = contentDao.getContentUrl(contentId);

        assertThat(url, is(equalTo(fileURi)));
    }

    @Test
    public void getContentDetailTest() throws Exception {
        UserDao userDao = new UserDao(dataSource(), new DomainDaoMock());
        DomainDao domainDao = new DomainDao(dataSource(), new I18nDaoMock());
        this.contentDao = new ContentDao(dataSource(), userDao, domainDao, new ElasticSearchDaoMock(), new TagDaoMock());

        // Init database
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetFilesPath() + daoDataSetPath + "getContentDetail.xml"));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
        } finally {
            databaseTester().getConnection().close();
        }
        ITable actualContentTable = dataSet.getTable("CONTENT");
        ITable actualContentCommentTable = dataSet.getTable("CONTENT_COMMENT");
        ITable actualContentDomainTable = dataSet.getTable("CONTENT_DOMAIN");

        Long contentId = 1L;

        /* Check initial row count */
        assertThat(actualContentTable.getRowCount(), is(equalTo(1)));
        assertThat(actualContentCommentTable.getRowCount(), is(equalTo(1)));

        ContentDetail contentDetail = contentDao.getContentDetail(contentId);

        assertThat(contentDetail.header().id().toString(), is(equalTo(actualContentTable.getValue(0, "ID"))));
        assertThat(contentDetail.header().author().id().toString(), is(equalTo(actualContentTable.getValue(0, "AUTHOR_REF"))));
        assertThat(contentDetail.header().ancestorId().toString(), is(equalTo(actualContentTable.getValue(0, "CONTENT_ANCESTOR_REF"))));
        assertThat(contentDetail.header().type().ordinal() + "", is(equalTo(actualContentTable.getValue(0, "CONTENT_TYPE"))));
        assertThat(contentDetail.header().creationDate().toString(), is(equalTo(actualContentTable.getValue(0, "CREATION_DATE").toString())));
        assertThat(contentDetail.header().description(), is(equalTo(actualContentTable.getValue(0, "DESCRIPTION").toString())));
        assertThat(contentDetail.url(), is(equalTo(actualContentTable.getValue(0, "FILE_URI"))));
        assertThat(contentDetail.header().lastModificationDate().toString(), is(equalTo(actualContentTable.getValue(0, "LAST_MODIFICATION_DATE").toString())));
        assertThat(contentDetail.header().popularity().toString(), is(equalTo(actualContentTable.getValue(0, "POPULARITY"))));
        assertThat(contentDetail.publicationComments(), is(equalTo(actualContentCommentTable.getValue(0, "PUBLICATION_COMMENTS"))));
        assertThat(contentDetail.rejectionComments(), is(equalTo(actualContentCommentTable.getValue(0, "REJECTION_COMMENTS"))));
        assertThat(contentDetail.header().endPublicationDate().toString(), is(equalTo(actualContentTable.getValue(0, "PUBLICATION_END_DATE").toString())));
        assertThat(contentDetail.header().startPublicationDate().toString(), is(equalTo(actualContentTable.getValue(0, "PUBLICATION_START_DATE").toString())));
        assertThat(contentDetail.header().status().ordinal() + "", is(equalTo(actualContentTable.getValue(0, "STATUS").toString())));
        assertThat(contentDetail.header().tags().size(),  is(equalTo(3)));
        assertThat(contentDetail.header().title(), is(equalTo(actualContentTable.getValue(0, "TITLE"))));
        assertThat(contentDetail.header().version().toString(), is(equalTo(actualContentTable.getValue(0, "VERSION"))));
        /* Check tags*/ 
        String[] databaseTags = actualContentTable.getValue(0, "TAGS").toString().split("\\;");
        assertThat(contentDetail.header().tags().get(0), is(equalTo(databaseTags[0])));
        assertThat(contentDetail.header().tags().get(1), is(equalTo(databaseTags[1])));
        assertThat(contentDetail.header().tags().get(2), is(equalTo(databaseTags[2])));
        /* Check domains*/
        assertThat(contentDetail.header().domains().size(), is(equalTo(2)));
        assertThat(contentDetail.header().domains().get(0).id().toString(), is(equalTo(actualContentDomainTable.getValue(0, "DOMAIN_REF"))));
        assertThat(contentDetail.header().domains().get(1).id().toString(), is(equalTo(actualContentDomainTable.getValue(1, "DOMAIN_REF"))));
        /* Check comments */
        assertThat(contentDetail.publicationComments(), is(equalTo(actualContentCommentTable.getValue(0, "PUBLICATION_COMMENTS"))));
        assertThat(contentDetail.rejectionComments(), is(equalTo(actualContentCommentTable.getValue(0, "REJECTION_COMMENTS"))));
    }

}
