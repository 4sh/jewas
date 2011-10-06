package fr.fsh.bbeeg.tag.resources;

import fr.fsh.bbeeg.tag.persistence.TagDao;
import fr.fsh.bbeeg.tag.pojos.Tag;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carmarolli
 * Date: 04/10/11
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class TagResource {

    private TagDao tagDao;

    public TagResource(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public void fetchAllTags(List<Tag> results) {
        tagDao.fetchAllTags(results);
    }

}
