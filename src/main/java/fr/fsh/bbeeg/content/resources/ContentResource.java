package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.resources.Author;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.AdvancedSearchQueryObject;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.content.pojos.ContentTypeResultObject;
import fr.fsh.bbeeg.content.pojos.SimpleSearchQueryObject;
import fr.fsh.bbeeg.user.pojos.User;
import org.joda.time.DateMidnight;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author driccio
 */
public class ContentResource {
    private ContentDao contentDao;
    private String contentPath;

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

    public void fetchAuthors(List<Author> authors, LimitedOrderedQueryObject loqo) {
        int count;

        if ("all".equals(loqo.ordering())) {
            count = 25;
        } else {
            count = loqo.number();
        }

        for (int i = 0; i < count; i++) {
            Author author = new Author();
            author.id(new Long(i)).name("Auteur " + i);
            authors.add(author);
        }
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

    public void updateContentOfContent(Long contentId, String contentType, ByteBuffer content) {
        // TODO: take into account contentType
        String url = contentPath + contentId + ".txt";
        Path path = Paths.get(url);

        try {
            Files.write(path, content.array());
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentDao.updateContentOfContent(contentId, contentType, url);
    }

    public InputStream getContentOfContent(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || "".equals(url)) {
            return null;
        }

        Path path = Paths.get(url);
        InputStream content = null;

        try {
            content = Files.newInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public void fetchSearch(List<ContentHeader> results, SimpleSearchQueryObject query) {
        contentDao.fetchSearch(results, query);
    }

    public void fetchSearch(List<ContentHeader> results, AdvancedSearchQueryObject query) {
        Long offset = new Long(query.startingOffset());

        for(int i=0; i<query.numberOfContents(); i++){
            results.add(new ContentHeader().id(offset)
                    .author(new User().name("fcamblor"))
                    .title("Contenu " + offset)
                    .creationDate(new DateMidnight().toDate())
                    .type(ContentType.TEXT)
                    .description("blablabla"));
            offset++;
        }

        if (query.authors() != null) {
            results.add(new ContentHeader().id(offset)
                    .author(new User().name("fcamblor"))
                    .title("Contenu (caché) " + offset)
                    .creationDate(new DateMidnight().toDate())
                    .type(ContentType.TEXT)
                    .description("blablabla"));
            offset++;
        }

        // TODO: uncomment this line when the mock will be removed
        //contentDao.fetchSearch(results, query);
    }

    public void fetchContents(List<ContentHeader> contentHeaders, User user) {
        contentDao.fetchContents(contentHeaders, user);
    }

    public void updateContentStatus(Long id, ContentStatus status) {
        contentDao.updateContentStatus(id, status);
    }
}
