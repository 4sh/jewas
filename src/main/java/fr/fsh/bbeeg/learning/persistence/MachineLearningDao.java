package fr.fsh.bbeeg.learning.persistence;

import fr.fsh.bbeeg.learning.pojos.MachineLearning;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: florent
 * Date: 4/5/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachineLearningDao {

    private static final Logger logger = LoggerFactory.getLogger(MachineLearningDao.class);

    private QueryTemplate<MachineLearning> mlQueryTemplate;

    public MachineLearningDao(DataSource dataSource) {
        init(dataSource);
    }

    private void init(DataSource dataSource){
        //Initializing QueryTemplates
        this.mlQueryTemplate = new QueryTemplate<MachineLearning>(dataSource, new MachineLearningRowMapper())
                .addQuery("getContentTags","select TAGS from CONTENT " +
                          "where ID = :id")
                .addQuery("getContentDomain", "select ID from DOMAIN" +
                          "where DOMAIN.ID=CONTENT_DOMAIN.DOMAIN_REF" +
                          "and CONTENT_DOMAIN.CONTENT_REF = :id"  )
                .addQuery("getContentAuthor", "select ID from USER " +
                          "where USER.ID = CONTENT.AUTHOR_REF" +
                          "and CONTENT.ID = :id");
    }



    private class MachineLearningRowMapper implements RowMapper<MachineLearning> {
        @Override
        public MachineLearning processRow(ResultSet rs) throws SQLException {
            return new MachineLearning();
                   //TODO: add MachineLearning().parameters(param)
        }
    }

    public String[] fetchContentDomain(long itemID){
        String domain = mlQueryTemplate.selectString("getContentDomain",
                new QueryExecutionContext().buildParams().bigint("id", itemID).toContext());
        return domain.split(";");
    }

    public Long fetchContentAuthor(long itemID){
        Long author = mlQueryTemplate.selectLong("getContentAuthor",
                new QueryExecutionContext().buildParams().bigint("id",itemID).toContext());
        return author;
    }

     public String[] fetchContentTags(long itemID){
         String tags = mlQueryTemplate.selectString("getContentTags",
                new QueryExecutionContext().buildParams().bigint("id",itemID).toContext());
         return tags.split(";");
     }
}