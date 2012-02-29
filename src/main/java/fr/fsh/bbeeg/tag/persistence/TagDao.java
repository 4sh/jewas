package fr.fsh.bbeeg.tag.persistence;

import fr.fsh.bbeeg.tag.pojos.Tag;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carmarolli
 * Date: 04/10/11
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class TagDao {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(TagDao.class);

    private QueryTemplate<Tag> tagQueryTemplate;

    public TagDao(DataSource dataSource) {
        init(dataSource);
    }

    private void init(DataSource dataSource) {

        // Initializing QueryTemplates
        this.tagQueryTemplate =
                new QueryTemplate<Tag>(dataSource, new TagRowMapper())
                    .addQuery("selectAll",
                            "select * from TAGS")
                    .addQuery("insert",
                            "insert into TAGS (TAG, WEIGHT) values (:tag, :weight)")
                    .addQuery("updateWeight",
                            "update TAGS set WEIGHT = :weight where TAG = :tag")
                    .addQuery("findTagWeight",
                            "select WEIGHT from TAGS where TAG = :tag")
                    .addQuery("delete",
                            "delete from TAGS where TAG = :tag")
                    .addQuery("selectLimitedPopular",
                            "select * from TAGS order by lower(TAG) " +
                            "limit :limit");
    }

    private class TagRowMapper implements RowMapper<Tag> {
        @Override
        public Tag processRow(ResultSet rs) throws SQLException {
            return new Tag()
                    .tag(rs.getString("TAG"))
                    .weight(rs.getLong("WEIGHT"));
        }
    }

    /**
     * Load the list with all tags registered in the system.
     *
     * @param tags the list of tags
     */
    public void fetchAllTags(List<Tag> tags) {
        tagQueryTemplate.select(tags, "selectAll", new QueryExecutionContext().buildParams().toContext());
    }

    /**
     * Load the list of the most popular tags.
     *
     * @param tags the list of the most popular tags to be loaded.
     * @param limit the number of tags to load.
     */
    public void fetchPopularTags(List<Tag> tags, int limit) {
        tagQueryTemplate.select(tags, "selectLimitedPopular",
                new QueryExecutionContext().buildParams()
                        .integer("limit", limit)
                        .toContext());
    }

    /**
     * Creates a new tag entry if not existing yet in database, or add 1 to the given tag weight.
     *
     * @param tag the tag being updated
     */
    public void createOrUpdateTag(String tag) {
        Long weight = tagQueryTemplate.selectLong("findTagWeight", new QueryExecutionContext().
                buildParams().
                string("tag", tag).
                toContext());

        if (weight == null) {
            tagQueryTemplate.insert("insert", new QueryExecutionContext().
                    buildParams().
                    string("tag", tag).
                    bigint("weight", 1).
                    toContext());
        } else {
            tagQueryTemplate.update("updateWeight", new QueryExecutionContext().
                    buildParams().
                    string("tag", tag).
                    bigint("weight", weight + 1).
                    toContext());
        }
    }

    /**
     * Deletes the given tag if its weight is 1 or decreases its weight by one.
     *
     * @param tag the tag that is being updated
     */
    public void deleteOrUpdateTag(String tag) {
        Long weight = tagQueryTemplate.selectLong("findTagWeight", new QueryExecutionContext().
                buildParams().
                string("tag", tag).
                toContext());
        if (weight == null) {
            logger.error("Tag weight is null for tag: {}", tag);
        } else if (weight == 1) {
            tagQueryTemplate.delete("delete", new QueryExecutionContext().
                    buildParams().
                    string("tag", tag).
                    toContext());
        } else {
            tagQueryTemplate.update("updateWeight", new QueryExecutionContext().
                    buildParams().
                    string("tag", tag).
                    bigint("weight", weight - 1).
                    toContext());
        }
    }
}
