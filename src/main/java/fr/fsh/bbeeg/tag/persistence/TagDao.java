package fr.fsh.bbeeg.tag.persistence;

import fr.fsh.bbeeg.tag.pojos.Tag;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

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

    private QueryTemplate<Tag> tagQueryTemplate;

    public TagDao(DataSource dataSource) {
        init(dataSource);
    }

    private void init(DataSource dataSource) {

        // Initializing QueryTemplates
        this.tagQueryTemplate =
                new QueryTemplate<Tag>(dataSource, new TagRowMapper())
                    .addQuery("selectAll", "SELECT * FROM TAGS")
                    .addQuery("insert", "INSERT INTO TAGS (TAG, WEIGHT) VALUES (:tag, :weight)")
                    .addQuery("updateWeight", "UPDATE TAGS SET WEIGHT = :weight WHERE TAG = :tag")
                    .addQuery("findTagWeight", "SELECT WEIGHT FROM TAGS WHERE TAG = :tag")
                    .addQuery("selectLimitedPopular", "SELECT * FROM (SELECT * FROM TAGS ORDER BY lower(TAG)) WHERE ROWNUM<= :limit");
    }

    private class TagRowMapper implements RowMapper<Tag> {
        @Override
        public Tag processRow(ResultSet rs) throws SQLException {
            return new Tag()
                    .tag(rs.getString("TAG"))
                    .weight(rs.getLong("WEIGHT"));
        }
    }

    public void fetchAllTags(List<Tag> tags) {
        tagQueryTemplate.select(tags, "selectAll", new QueryExecutionContext().buildParams().toContext());
    }

    public void fetchPopularTags(List<Tag> tags, int limit) {
        tagQueryTemplate.select(tags, "selectLimitedPopular",
                new QueryExecutionContext().buildParams()
                        .integer("limit", limit)
                        .toContext());
    }

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
}
