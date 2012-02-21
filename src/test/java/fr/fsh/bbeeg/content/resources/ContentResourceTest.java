package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.AbstractBBEEGTest;
import fr.fsh.bbeeg.common.CliOptions;
import fr.fsh.bbeeg.common.CliOptionsMock;
import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.persistence.mocks.*;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author carmarolli
 */
public class ContentResourceTest extends AbstractBBEEGTest {

    private static final Logger logger = LoggerFactory.getLogger(ContentResourceTest.class);
    
    private static String contentFileRepository = null;
    private static String tmpContentFileRepository = null;
    
    private ContentResource contentResource;
    private String initDataSetsPath = "/initDataSet/";
    private String filename;
    
    private static String getTestFileRepository(String fileRepository) {
        if (fileRepository == null) {
            throw new IllegalArgumentException("No file repository specified, cannot install test file repository");
        }
        return fileRepository.concat("test/");
    }

    @BeforeClass
    public static void setUp() throws Exception {
        AbstractBBEEGTest.setUp();
        //contentFileRepository = "/Users/carmarolli/.bbeeg/test/content/";
        //tmpContentFileRepository = "/Users/carmarolli/.bbeeg/test/tmp/";
        CliOptions options = new CliOptionsMock()
                .contentFileRepository(contentFileRepository)
                .tmpContentFileRepository(tmpContentFileRepository);

        BBEEGConfiguration.INSTANCE.cliOptions(options);
        
        // Create test directories 
        contentFileRepository = Files.createTempDirectory("tmp").toString();
        tmpContentFileRepository = Files.createTempDirectory("tmp").toString();
    }

   /**
    * Test case : New file is uploaded and needs to be moved from tmp folder to content repository folder
    */
    @Test
    public void updateContentOfContentTest() throws Exception {
        try {
            // Setup file in test temporary files repository
            filename = TempFiles.store("Text content of the test file");

            // Initialize ContentDao
            ContentDao contentDao = new ContentDao(dataSource(), null, new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());

            contentResource = new ContentResource(contentDao, new I18nDaoMock(), contentFileRepository);

            // Init database
            IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetsPath + "initUpdateContentOfContentTest.xml"));
            ITable actualTable = dataSet.getTable("CONTENT");

            try
            {
                DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
            }
            finally
            {
                databaseTester().getConnection().close();
            }

            Long contentId = Long.parseLong((String) actualTable.getValue(0, "ID"));
            contentResource.updateContentOfContent(contentId, filename, false);

            // Assert that the file has been removed from tmp folder
            assertThat(TempFiles.tmpFileExists(filename), is(false));

            // Assert that the file has been moved to the file content repository and renamed with the content id
            String fileUrl = contentFileRepository + contentId + ".txt";
            assertThat(Files.exists(Paths.get(fileUrl)), is(true));
            assertThat(actualTable.getRowCount(), is(equalTo(1)));
            assertThat(actualTable.getValue(0, "ID").toString(), is(equalTo("0")));

            // Assert actual database table has been updated correctly (file url of the updated content)
            String contentUrlFromDB = contentDao.getContentUrl(contentId);
            assertThat(contentUrlFromDB, is(equalTo(fileUrl)));

        } catch (DataSetException e) {
            logger.error("Error on loading test data set into db", e);
        }
    }

    /**
     * Test case : Test copy ancestor content file and link it to the descendant content.  
     */
    @Test
    public void copyContentOfContentTest() throws Exception {
        try {
            /* Setup file in test content files repository */
            filename = "10.test";
            String ancestorContentFileURI = contentFileRepository + filename;
            Files.createFile(Paths.get(ancestorContentFileURI));

            /* Assert that the file has been created in the test content directory */
            assertThat(Files.exists(Paths.get(ancestorContentFileURI)), is(true));

            /* Initialize database state corresponding to the file system state */
            IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource(initDataSetsPath + "initCopyContentOfContentTest.xml"));
            ITable actualTable = dataSet.getTable("CONTENT");
            assertThat(actualTable.getValue(0, "ID").toString(), is(equalTo("11")));
            assertThat(actualTable.getRowCount(), is(equalTo(1)));

            try
            {
                DatabaseOperation.CLEAN_INSERT.execute(databaseTester().getConnection(), dataSet);
            }
            finally
            {
                databaseTester().getConnection().close();
            }

            Long contentId = Long.parseLong((String) actualTable.getValue(0, "ID"));

            /* Initialize ContentDao and ContentResource */
            ContentDao contentDao = new ContentDao(dataSource(), null, new UserDaoMock(), new DomainDaoMock(), new ElasticSearchDaoMock(), new TagDaoMock());
            contentResource = new ContentResource(contentDao, new I18nDaoMock(), contentFileRepository);

            /* Update the FILE_URI in database because of the execution env dependency */
            contentDao.updateContentUrl(contentId, ancestorContentFileURI);

            contentResource.copyContentOfContent(contentId);

            String duplicatedContentFileURI = contentFileRepository + contentId + ".file";
            /* Assert that the file is still in the ancestor content test directory */
            assertThat(Files.exists(Paths.get(ancestorContentFileURI)), is(true));
            /* Assert that the file has also been copied to the file content repository and renamed with the new content id */
            assertThat(Files.exists(Paths.get(duplicatedContentFileURI)), is(true));

            /* Assert actual database table has been updated correctly (file uri of the updated content) */
            assertThat(actualTable.getValue(0, "FILE_URI").toString(), is(equalTo(duplicatedContentFileURI)));
        } catch (DataSetException | IOException e) {
            logger.error("Error on loading test data set into db", e);
        }
    }

    @After
    public void after() {
        filename = null;
        contentResource = null;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        AbstractBBEEGTest.tearDown();
        Files.deleteIfExists(Paths.get(contentFileRepository));
        Files.deleteIfExists(Paths.get(tmpContentFileRepository));
    }

}
