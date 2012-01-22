package fr.fsh.bbeeg.content.persistence.mocks;

import fr.fsh.bbeeg.tag.persistence.TagDao;
import fr.fsh.bbeeg.tag.pojos.Tag;

import java.util.List;

public class TagDaoMock extends TagDao {

    public TagDaoMock() {
        super(null);
    }

    public void fetchAllTags(List<Tag> tags) {

    }

    public void fetchPopularTags(List<Tag> tags, int limit) {

    }

    public void createOrUpdateTag(String tag) {

    }
}
