package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.resources.Author;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.content.pojos.ContentTypeResultObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author driccio
 */
public class ContentResource {
    private ContentDao contentDao;

    public ContentResource(ContentDao _contentDao) {
        contentDao = _contentDao;
    }

    public List<ContentHeader> getAddedContent(LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        return contentDao.getRecentContent(loqo.number());
    }

    public List<ContentHeader> getPopularContent(LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        return contentDao.getPopularContent(loqo.number());
    }

    public List<ContentHeader> getViewedContent(LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        return contentDao.getLastViewedContent(loqo.number());
    }

    public Count getContentCount() {
        return contentDao.getTotalNumberOfContent();
    }

    public ContentDetail getContentDetail(Long id) {
        return contentDao.getContentDetail(id);
    }

    public List<Author> getAuthor(LimitedOrderedQueryObject loqo) {
        List<Author> list = new ArrayList<Author>();
        int count;

        if ("all".equals(loqo.ordering())) {
            count = 25;
        } else {
            count = loqo.number();
        }

        for (int i = 0; i < count; i++) {
            Author author = new Author();
            author.id(new Long(i)).name("Auteur " + i);
            list.add(author);
        }

        return list;
    }

    public List<ContentTypeResultObject> getContentType(LimitedOrderedQueryObject loqo) {
        // TODO: check the ordering property
        List<ContentTypeResultObject> results = new ArrayList<ContentTypeResultObject>();
        ContentType[] contentTypes = ContentType.values();

        for (int i = 0; i < contentTypes.length; i++) {
            results.add(new ContentTypeResultObject().id(new Long(i)).title(contentTypes[i].name()));
        }

        return results;
    }

//    public ContentHeader getContentById(Long contentId){
//        ContentHeader c = new ContentHeader();
//        c.id(Long.valueOf(1234));
//        c.author(new User().name("4sh"));
//        c.title("Lorem Ipsum");
//        c.description("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas luctus lectus sed nulla " +
//                "vestibulum nec volutpat ante ultrices. Etiam sed neque ipsum. Nulla nulla nisl, rutrum vel luctus " +
//                "at, hendrerit at tellus. Aliquam rutrum risus eget libero porta congue. Nunc porta augue in " +
//                "felis fringilla nec accumsan nunc aliquam. Cras ac volutpat arcu. Phasellus diam erat, rutrum " +
//                "in scelerisque eu, convallis a diam. Aenean sodales tellus sed lectus tempus pellentesque. " +
//                "Suspendisse et est metus, sit amet blandit urna. Pellentesque habitant morbi tristique senectus " +
//                "et netus et malesuada fames ac turpis egestas. Nullam vitae sollicitudin diam.");
//
//        return c;
//    }

    public void updateContent(ContentDetail contentDetail) {
//        ContentDetail content = new ContentDetail();
//
//        content.header().title(title)
//                .description(description)
//                .domains(domains);

        contentDao.updateContent(contentDetail);
    }

    public Long createContent(ContentDetail contentDetail) {
        return contentDao.createContent(contentDetail);
    }

    public void updateContentOfContent(Long contentId, String contentType, ByteBuffer content) {
        // TODO: take into account contentType
        String url = "D:\\docs\\BBEEG\\contents\\file" + contentId + ".txt";
        Path path = Paths.get(url);

        try {
            Files.write(path, content.array());
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentDao.updateContentOfContent(contentId, contentType, url);
    }

    public InputStream getContentOfContent(Long contentId) {
        String url = "D:\\docs\\BBEEG\\contents\\file" + contentId + ".txt";
        Path path = Paths.get(url);
        InputStream content = null;

        try {
            content = Files.newInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
}
