package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.resources.Author;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;

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

        for (int i = 0; i < loqo.number(); i++) {
            Author author = new Author();
            author.name("Auteur "+i);
            list.add(author);
        }

        return list;
    }
}
