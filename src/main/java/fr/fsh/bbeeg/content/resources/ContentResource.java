package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.content.pojos.CommentType;
import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.*;
import fr.fsh.bbeeg.i18n.persistence.I18nDao;
import fr.fsh.bbeeg.user.pojos.User;
import jewas.http.data.FileUpload;

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
        return contentDao.getContentDetail(id);
    }

    /**
     * Fetch all the available content types.
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

    public void updateContent(ContentDetail contentDetail) {
        contentDao.updateContent(contentDetail);
    }

    public Long createContent(ContentDetail contentDetail) {
        return contentDao.createContent(contentDetail);
    }

    public String temporaryUpdateContentOfContent(String text) {
        return TempFiles.store(text);
    }

    public String temporaryUpdateContentOfContent(FileUpload fileUpload, String extension) {
        return TempFiles.store(fileUpload, extension);
    }

    public void updateContentOfContent(Long contentId, String fileId) {
        Path sourcePath = TempFiles.getPath(fileId);

        String extension = fileId.split("\\.")[1];
        String url = contentPath + contentId + "." + extension;
        Path targetPath = Paths.get(url);

        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentDao.updateContentOfContent(contentId, targetPath.toString());
    }

    public Path getContentOfContent(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || "".equals(url)) {
            return null;
        }

        return Paths.get(url);
    }

    public String getContentOfContentExtension(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || "".equals(url)) {
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

    public void updateContentStatus(Long id, ContentStatus status, Date startPublicationDate, Date endPublicationDate, String comment) {

        CommentType commentType = null;
        if (ContentStatus.TO_BE_VALIDATED.equals(status)) {
            commentType = CommentType.PUBLICATION;
        } else if (ContentStatus.REJECTED.equals(status)) {
            commentType = CommentType.REJECTION;
        }
        contentDao.updateContentStatus(id, status, startPublicationDate, endPublicationDate, comment, commentType);
    }

    public void reIndexAllInElasticSearch() {
        contentDao.reIndexAllInElasticSearch();
    }

    /**
     * Increment the stored count of views for this content.
     * @param contentId the content identifier.
     */
    public void incrementPopularity(Long contentId) {
        contentDao.incrementPopularity(contentId);
    }

}
