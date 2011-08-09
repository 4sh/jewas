package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.resources.Author;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.content.pojos.Content;
import fr.fsh.bbeeg.content.pojos.TextContent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author driccio
 */
public class ContentResource {
    public static List<ContentSearchResult> getAddedContent(LimitedOrderedQueryObject loqo) {
        List<ContentSearchResult> list = new ArrayList<ContentSearchResult>();

        for (int i = 0; i < loqo.number(); i++) {
            ContentSearchResult csr = new ContentSearchResult();
            csr.id(""+i).title("Nouveau contenu "+i);
            list.add(csr);
        }

        return list;
    }

    public static List<ContentSearchResult> getPopularContent(LimitedOrderedQueryObject loqo) {
        List<ContentSearchResult> list = new ArrayList<ContentSearchResult>();

        for (int i = 0; i < loqo.number(); i++) {
            ContentSearchResult csr = new ContentSearchResult();
            csr.id(""+i).title("Contenu "+i);
            list.add(csr);
        }

        return list;
    }

    public static List<ContentSearchResult> getViewedContent(LimitedOrderedQueryObject loqo) {
        List<ContentSearchResult> list = new ArrayList<ContentSearchResult>();

        for (int i = 0; i < loqo.number(); i++) {
            ContentSearchResult csr = new ContentSearchResult();
            csr.id(""+i).title("Contenu visualisé "+i);
            list.add(csr);
        }

        return list;
    }

    public static Count getContentCount() {
        return new Count(367);
    }

    public static List<Author> getAuthor(LimitedOrderedQueryObject loqo) {
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

    public static List<ContentType> getContentType(LimitedOrderedQueryObject loqo) {
        List<ContentType> list = new ArrayList<ContentType>();
        int count;

        if ("all".equals(loqo.ordering())) {
            count = 18;
        } else {
            count = loqo.number();
        }

        for (int i = 0; i < count; i++) {
            ContentType type = new ContentType();
            type.id(new Long(i)).title("Type " + i);
            list.add(type);
        }

        return list;
    }

    public static Content getContentById(Long contentId){
        TextContent c = new TextContent();
        c.id(Long.valueOf(1234));
        c.author("4sh");
        c.title("Lorem Ipsum");
        c.text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas luctus lectus sed nulla " +
                "vestibulum nec volutpat ante ultrices. Etiam sed neque ipsum. Nulla nulla nisl, rutrum vel luctus " +
                "at, hendrerit at tellus. Aliquam rutrum risus eget libero porta congue. Nunc porta augue in " +
                "felis fringilla nec accumsan nunc aliquam. Cras ac volutpat arcu. Phasellus diam erat, rutrum " +
                "in scelerisque eu, convallis a diam. Aenean sodales tellus sed lectus tempus pellentesque. " +
                "Suspendisse et est metus, sit amet blandit urna. Pellentesque habitant morbi tristique senectus " +
                "et netus et malesuada fames ac turpis egestas. Nullam vitae sollicitudin diam.");

        return c;
    }
}
