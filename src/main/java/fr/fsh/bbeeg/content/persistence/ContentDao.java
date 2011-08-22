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
import java.util.Map;

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
                        .addQuery("count", "select count(*) as COUNT from Content")
                        .addQuery("insertForCreation", "INSERT INTO CONTENT (ID, CREATION_DATE, LAST_MODIFICATION_DATE, PUBLISHED, CONTENT_TYPE, AUTHOR_REF) " +
                                "VALUES (CONTENT_SEQ.nextval, CURRENT_DATE, CURRENT_DATE, 0, :contentType, :authorId)")
                        .addQuery("updateContentUrl", "UPDATE CONTENT SET FILE_URI = :url WHERE ID = :id")
                        .addQuery("updateContent", "UPDATE CONTENT SET TITLE = :title, DESCRIPTION = :description WHERE ID = :id")
                        .addQuery("addLinkWithDomain", "INSERT INTO CONTENT_DOMAIN (CONTENT_REF, DOMAIN_REF) VALUES (:contentId, :domainId)")
                        .addQuery("removeLinkWithDomain", "DELETE FROM CONTENT_DOMAIN WHERE CONTENT_REF = :contentId AND DOMAIN_REF = :domainId");

//        this.contentDetailQueryTemplate = new QueryTemplate<ContentDetail>(dataSource, new ContentDetailRowMapper())
//                        .addQuery("selectById", "select * from Content where id = :id");

        this.idQueryTemplate =
                new QueryTemplate<Long>(dataSource, new LongRowMapper())
                        .addQuery("selectDomainIdsByContentId",
                                "select domain_ref as ID from Content_Domain " +
                                        "where content_ref = :id");
    }

    public ContentDetail getContentDetail(Long id) {
        // TODO
//        ContentHeader ctr = contentHeaderQueryTemplate.selectObject("selectById",
//                new QueryExecutionContext().buildParams()
//                        .bigint("id", id)
//                        .toContext()
//        );

        return null;
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

    public Long createContent(ContentType contentType) {
        Map<String, String> genKeys =
                contentHeaderQueryTemplate.insert("insertForCreation",
                        new QueryExecutionContext().buildParams()
                                .integer("contentType", contentType.ordinal())
                                .bigint("authorId", 1000) // TODO: change 0 with the current connected user id
                                .toContext(),
                        "id");
        return Long.valueOf(genKeys.get("id"));
    }

    public void updateContentOfContent(Long contentId, String contentType, String url) {
        contentHeaderQueryTemplate.update("updateContentUrl",
                new QueryExecutionContext().buildParams()
                        .string("url", url)
                        .bigint("id", contentId)
                        .toContext());
        // TODO: check the number of row updated.
    }

    public void updateContent(ContentDetail contentDetail) {
        contentHeaderQueryTemplate.update("updateContent",
                new QueryExecutionContext().buildParams()
                        .string("title", contentDetail.header().title())
                        .string("description", contentDetail.header().description())
                        .bigint("id", contentDetail.header().id())
                        .toContext());

        // Get current domain ids that are linked with the content.
        List<Long> domainsIds = getDomainIds(contentDetail.header().id());

        for (Domain domainToCheck : contentDetail.header().domains()) {
            if (!domainsIds.contains(domainToCheck.id())) {
                contentHeaderQueryTemplate.insert("addLinkWithDomain",
                        new QueryExecutionContext().buildParams()
                                .bigint("contentId", contentDetail.header().id())
                                .bigint("domainId", domainToCheck.id())
                                .toContext());
            }
        }

        for (Long domainIdToCheckForRemove : domainsIds) {
            boolean found = false;

            for (Domain domain : contentDetail.header().domains()) {
                if (domainIdToCheckForRemove == domain.id()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                contentHeaderQueryTemplate.delete("removeLinkWithDomain",
                        new QueryExecutionContext().buildParams()
                                .bigint("contentId", contentDetail.header().id())
                                .bigint("domainId", domainIdToCheckForRemove)
                                .toContext());
            }
        }
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
        return domainDao.getDomains(getDomainIds(contentId));
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
