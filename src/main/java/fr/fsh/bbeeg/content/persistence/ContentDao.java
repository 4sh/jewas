package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.persistence.UserDao;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.LongRowMapper;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author driccio
 */
public class ContentDao {
    private QueryTemplate<ContentHeader> contentHeaderQueryTemplate;
    private QueryTemplate<ContentDetail> contentDetailQueryTemplate;
    private QueryTemplate<Long> idQueryTemplate;
    private UserDao userDao;
    private DomainDao domainDao;

    public ContentDao(DataSource dataSource, UserDao _userDao, DomainDao _domainDao) {
        userDao = _userDao;
        domainDao = _domainDao;

        this.contentHeaderQueryTemplate =
                new QueryTemplate<ContentHeader>(dataSource, new ContentRowMapper())
                        .addQuery("selectById", "select * from Content where id = :id")
                        .addQuery("selectAll", "select * from Content")
                        .addQuery("selectLimitedRecent",
                                "select * from " +
                                        "(select * from Content order by id desc) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectLimitedPopular", // TODO: change request or remove it. Use elasticSearch insteed
                                "select * from " +
                                        "(select * from Content) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectLimitedLastViewed", // TODO: change request or remove it. Use elasticSearch insteed
                                "select * from " +
                                        "(select * from Content) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("count", "select count(*) as COUNT from Content");

//        this.contentDetailQueryTemplate = new QueryTemplate<ContentHeader>(dataSource, new ContentRowMapper())
//                        .addQuery("selectById", "select * from Content where id = :id");

        this.idQueryTemplate =
                new QueryTemplate<Long>(dataSource, new LongRowMapper())
                        .addQuery("selectDomainIdsByContentId",
                                "select domain_ref as ID from Content_Domain " +
                                        "where content_ref = :id");
    }

    public ContentHeader getContentToRead(Long id) {
        ContentHeader ctr = contentHeaderQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", id)
                        .toContext()
        );

        return ctr;
    }

    public List<ContentHeader> getAllContentToRead() {
        List<ContentHeader> entries = new ArrayList<ContentHeader>();
        contentHeaderQueryTemplate.select(entries, "selectAll",
                new QueryExecutionContext().buildParams().toContext()
        );

        return entries;
    }

    public List<ContentHeader> getRecentContent(int limit) {
        List<ContentHeader> entries = new ArrayList<ContentHeader>();
        contentHeaderQueryTemplate.select(entries, "selectLimitedRecent",
                new QueryExecutionContext().buildParams().integer("limit", limit).toContext()
        );

        return entries;
    }

    public List<ContentHeader> getPopularContent(int limit) {
        List<ContentHeader> entries = new ArrayList<ContentHeader>();
        contentHeaderQueryTemplate.select(entries, "selectLimitedPopular",
                new QueryExecutionContext().buildParams().integer("limit", limit).toContext()
        );

        return entries;
    }

    public List<ContentHeader> getLastViewedContent(int limit) {
        List<ContentHeader> entries = new ArrayList<ContentHeader>();
        contentHeaderQueryTemplate.select(entries, "selectLimitedLastViewed",
                new QueryExecutionContext().buildParams().integer("limit", limit).toContext()
        );

        return entries;
    }

    public Count getTotalNumberOfContent() {
        return new Count().count(
                contentHeaderQueryTemplate.selectLong("count",
                        new QueryExecutionContext().buildParams().toContext()).intValue()
        );
    }

    private List<Long> getDomainIds(Long contentId) {
        List<Long> domainIds = new ArrayList<Long>();
        idQueryTemplate.select(domainIds, "selectDomainIdsByContentId",
                new QueryExecutionContext().buildParams()
                        .bigint("id", contentId)
                        .toContext()
        );

        return domainIds;
    }

    private List<Domain> getDomains(Long contentId) {
        return domainDao.getDomainsToRead(getDomainIds(contentId));
    }

    private class ContentRowMapper implements RowMapper<ContentHeader> {
        @Override
        public ContentHeader processRow(ResultSet rs) throws SQLException {
            return new ContentHeader()
                    .id(rs.getLong("ID"))
                    .title(rs.getString("TITLE"))
                    .description(rs.getString("DESCRIPTION"))
                    .published(rs.getBoolean("PUBLISHED"))
                    .creationDate(rs.getDate("CREATION_DATE"))
                    .lastModificationDate(rs.getDate("LAST_MODIFICATION_DATE"))
                    .type(ContentType.values()[rs.getInt("CONTENT_TYPE")])
                    .author(userDao.getUser(rs.getLong("AUTHOR_REF")))
                    .domains(getDomains(rs.getLong("ID")));
        }
    }
}
