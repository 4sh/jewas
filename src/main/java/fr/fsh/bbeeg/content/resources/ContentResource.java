package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.*;
import fr.fsh.bbeeg.i18n.persistence.I18nDao;
import fr.fsh.bbeeg.user.pojos.User;
import jewas.http.data.FileUpload;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

/**
 * @author driccio
 */
public class ContentResource {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ContentResource.class);

    /**
     * Data access object used to access database translations.
     */
    private final I18nDao i18nDao;
    private final ContentDao contentDao;
    private final String contentPath;

    public ContentResource(ContentDao _contentDao, I18nDao _i18nDao, String _contentPath) {
        this.i18nDao = _i18nDao;
        this.contentDao = _contentDao;
        this.contentPath = _contentPath;
    }

    public void fetchAddedContents(List<ContentHeader> contentHeaders, LimitedOrderedQueryObject loqo) {
        contentDao.fetchRecentContents(contentHeaders, loqo.number());
    }

    public void fetchPopularContents(List<ContentHeader> contentHeaders, LimitedOrderedQueryObject loqo) {
        contentDao.fetchPopularContent(contentHeaders, loqo.number());
    }

    public void fetchViewedContent(List<ContentHeader> contentHeaders, LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        contentDao.fetchLastViewedContent(contentHeaders, loqo.number());
    }

    public Count getContentCount() {
        return contentDao.getTotalNumberOfContent();
    }

    public ContentDetail getContentDetail(Long id) {
        ContentDetail contentDetail = contentDao.getContentDetail(id);
        incrementPopularity(id);
        updateLastConsultationDate(id);
        return contentDetail.url("/content/content/" + id);
    }

    /**
     * Fetch all the available content types.
     * 
     * @param results the list of {@ContentTypeResultObject}
     * @param loqo the {@LimitedOrderedQueryObject}
     */
    public void fetchContentTypes(List<ContentTypeResultObject> results, LimitedOrderedQueryObject loqo) {
        for(ContentType contentType : ContentType.values()) {
            results.add(new ContentTypeResultObject()
                            .id(new Long(contentType.ordinal()))
                            .title(i18nDao.translation(contentType.i18nKey(), "fr")));
        }
    }

    /**
     * Content insertion. Insert a new row corresponding to the content information in the relational database.
     *
     * @param contentDetail the information to store in database.
     * @return the inserted content identifier.
     */
    public Long createContent(ContentDetail contentDetail) {
        if (contentDetail.header().version() == null) {
            contentDetail.header().version(0);
        }
        return contentDao.createContent(contentDetail);
    }

    /**
     * Content edition. Depending on the current content status,
     * the content would be duplicated and the version number upgraded.
     *
     * @param contentDetail the content to update.
     */
    public void updateContent(ContentDetail contentDetail) {
        Long oldContentId = contentDetail.header().id();
        ContentDetail contentDetailFromDb = contentDao.getContentDetail(oldContentId);

        if (ContentStatus.VALIDATED.equals(contentDetailFromDb.header().status())
                || ContentStatus.REJECTED.equals(contentDetailFromDb.header().status())) {

            /* Upgrade version and link contents to build hierarchy */
            contentDetailFromDb.header().ancestorId(contentDetailFromDb.header().ancestorId());
            contentDetailFromDb.header().version(contentDao.getHigherVersionNumber(contentDetailFromDb.header().ancestorId()) + 1);
            /* Replicates modifications to the newly created content */
            contentDetailFromDb.header().title(contentDetail.header().title());
            contentDetailFromDb.header().description(contentDetail.header().description());
            contentDetailFromDb.header().domains(contentDetail.header().domains());
            contentDetailFromDb.header().tags(contentDetail.header().tags());
            contentDetailFromDb.header().creationDate(contentDetail.header().creationDate());
            Long newContentId = createContent(contentDetailFromDb);
            // Modifying an existing document but no upload performed, just duplicates the old version content url.
            if (contentDetail.url() == null || contentDetail.url().isEmpty()) {
                contentDao.updateContentUrl(newContentId, contentDao.getContentUrl(oldContentId));
            }
            contentDetail.header().id(newContentId);
        } else {
            if (ContentStatus.TO_BE_VALIDATED.equals(contentDetailFromDb.header().status())) {
                updateContentStatus(oldContentId, ContentStatus.DRAFT, null);
            }
            contentDetail.header().version(contentDetailFromDb.header().version());
            contentDao.updateContent(contentDetail);
        }
    }

    public String temporaryUpdateContentOfContent(String text) {
        return TempFiles.store(text);
    }

    public String temporaryUpdateContentOfContent(FileUpload fileUpload, String extension) {
        return TempFiles.store(fileUpload, extension);
    }

    /**
     * Updates the file content related to the content identified by the given content id.
     *
     * @param contentId   the unique database id of the content.
     * @param fileName    the file related to the current content.
     * @param postProcess whether the file content will need to be need post processed once stored on the persistence context.
     */
    public void updateContentOfContent(Long contentId, String fileName, Boolean postProcess) {

        Path sourcePath = TempFiles.getPath(fileName);
        String extension = jewas.util.file.Files.getFileExtension(fileName);
        String url = contentPath + contentId + "." + extension;
        Path targetPath = Paths.get(url);

        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            if (postProcess) {
                Files.copy(sourcePath,
                        Paths.get(BBEEGConfiguration.INSTANCE.cliOptions().videoEncodingInput() + "/" + fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            contentDao.updateContentOfContent(contentId, targetPath.toString());
        } catch (IOException e) {
            logger.error("Failed to move content from : {} to: {}", sourcePath, targetPath);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * In case of content duplication (after validated content modification),
     * duplicates the ancestor content file and link it to the new content represented by the given id.
     *
     * @param duplicatedContentId the new content id
     */
    public void copyContentOfContent(Long duplicatedContentId) {

        String fileName = contentDao.getContentUrl(duplicatedContentId);
        Path sourcePath = Paths.get(fileName);

        String extension = jewas.util.file.Files.getFileExtension(fileName);
        String url = contentPath + duplicatedContentId + "." + extension;
        Path targetPath = Paths.get(url);
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            contentDao.updateContentOfContent(duplicatedContentId, targetPath.toString());
        } catch (IOException e) {
            logger.error("Failed to copy content from : {} to: {}", sourcePath, targetPath);
            logger.error(e.getMessage(), e);
        }
    }
    
    public Path getContentOfContent(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || url.isEmpty()) {
            return null;
        }
        String filename = contentId + ".mp4";
        Path sourcePath = Paths.get(BBEEGConfiguration.INSTANCE.cliOptions().videoEncodingOutput() + "/" + filename);
        Path targetPath = Paths.get(url);
        if (Files.exists(sourcePath)) {
            try {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.warn("Cannot move encoded video {} to content {}", sourcePath.toString(), contentId);
            }
        }
        return Paths.get(url);
    }

    public String getContentOfContentExtension(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || url.isEmpty()) {
            return null;
        }

        int extIndex = url.lastIndexOf(".");
        if (extIndex == -1) {
            return "";
        } else {
            return url.substring(extIndex + 1);
        }
    }

    public void fetchSearch(List<ContentHeader> results, SimpleSearchQueryObject query) {
        contentDao.fetchSearch(results, query);
    }

    public void fetchSearch(List<ContentHeader> results, AdvancedSearchQueryObject query) {
        Long offset = new Long(query.startingOffset());
        contentDao.fetchSearch(results, query);
    }

    public void fetchContents(List<ContentHeader> contentHeaders, User user) {
        contentDao.fetchContents(contentHeaders, user);
    }

    /**
     * Update the status of the content identified by the given content identifier.
     * 
     * @param contentId the identifier of the content to update.
     * @param newStatus the new content status to apply
     * @param publicationDetails must not be null if status equals {@ContentStatus#TO_BE_VALIDATED} or {@ContentStatus#REJECTED}.
     * It will be ignored in other cases.
     */
    public void updateContentStatus(Long contentId, ContentStatus newStatus, ContentPublicationDetail publicationDetails) {

        if (publicationDetails == null
                && (ContentStatus.TO_BE_VALIDATED.equals(newStatus) || ContentStatus.TO_BE_DELETED.equals(newStatus))) {
            logger.error("Publication details must not be null to update current status of content: %s ", contentId + " to status: " + newStatus);
            return;
        }

        // Archive old content if necessary
        if (ContentStatus.VALIDATED.equals(newStatus)) {
            ContentDetail contentDetail = contentDao.getContentDetail(contentId);
            if (contentDetail.header().version() > 0 && contentDetail.header().ancestorId() != null) {
                contentDao.archivePreviousVersion(contentDetail.header().ancestorId());
            }
        }

        // Update content status
        contentDao.updateContentStatus(contentId, newStatus);

        // Depending to new status, update publication/rejection details
        Date currentDate = new DateMidnight().toDate();

        // Update publication dates
        if (ContentStatus.TO_BE_VALIDATED.equals(newStatus)
                && publicationDetails.start() != null
                && (publicationDetails.end() == null || (publicationDetails.start().before(publicationDetails.end())
                && publicationDetails.end().after(currentDate)))) {
            contentDao.updateContentPublicationDates(contentId, publicationDetails);
        }

        // Update comments if new content status is TO_BE_VALIDATED or REJECTED
        if ((ContentStatus.TO_BE_VALIDATED.equals(newStatus) || ContentStatus.REJECTED.equals(newStatus))
                && publicationDetails.comments() != null
                && !publicationDetails.comments().isEmpty()) {
            contentDao.updateContentPublicationComments(contentId, newStatus, publicationDetails);
        }
    }

    public void reIndexAllInElasticSearch() {
        contentDao.reIndexAllInElasticSearch();
    }

    /**
     * Increment the stored count of views for this content.
     *
     * @param contentId the content identifier.
     */
    private void incrementPopularity(Long contentId) {
        contentDao.incrementPopularity(contentId);
    }

    /**
     * Update the stored last consultation date for this content.
     *
     * @param contentId the content identifier.
     */
    private void updateLastConsultationDate(Long contentId) {
        contentDao.updateLastConsultationDate(contentId, new DateTime().toDate());
    }
   
}
