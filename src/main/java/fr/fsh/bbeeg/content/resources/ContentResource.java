package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.*;
import fr.fsh.bbeeg.user.pojos.User;
import jewas.http.data.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author driccio
 */
public class ContentResource {
    private final ContentDao contentDao;
    private final String contentPath;

    public ContentResource(ContentDao _contentDao, String _contentPath) {
        contentDao = _contentDao;
        contentPath = _contentPath;
    }

    public void fetchAddedContents(List<ContentHeader> contentHeaders, LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        contentDao.fetchRecentContents(contentHeaders, loqo.number());
    }

    public void fetchPopularContents(List<ContentHeader> contentHeaders, LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
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

    public void fetchContentTypes(List<ContentTypeResultObject> results, LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        ContentType[] contentTypes = ContentType.values();

        for (int i = 0; i < contentTypes.length; i++) {
            results.add(new ContentTypeResultObject().id(new Long(i)).title(contentTypes[i].name()));
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
        // TODO: take into account contentType
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
//
//     public void updateContentOfContent(Long contentId, String fileId) {
//        // TODO: take into account contentType
//        String url = contentPath + contentId + "." + extension;
//        Path path = Paths.get(url);
//
//         try {
//             fileUpload.toFile(path.toFile());
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//
//        contentDao.updateContentOfContent(contentId, contentType, url);
//    }

    public InputStream getContentOfContent(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || "".equals(url)) {
            return null;
        }

        Path path = Paths.get(url);
        InputStream stream = null;

        try {
            stream = Files.newInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream;
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

    public void updateContentStatus(Long id, ContentStatus status, String comment) {
        contentDao.updateContentStatus(id, status, comment);
    }

    public void reIndexAllInElasticSearch() {
        contentDao.reIndexAllInElasticSearch();
    }
}
