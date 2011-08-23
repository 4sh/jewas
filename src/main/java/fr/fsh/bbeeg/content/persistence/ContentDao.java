package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.content.pojos.SimpleSearchQueryObject;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.persistence.UserDao;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.LongRowMapper;
import jewas.persistence.rowMapper.RowMapper;
import org.joda.time.DateMidnight;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author driccio
 */
public class ContentDao {
    private QueryTemplate<ContentHeader> contentHeaderQueryTemplate;
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
                        .addQuery("selectUrl", "select FILE_URI from Content where id = :id")
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
                        .addQuery("simpleSearch",
                                "select * from " +
                                        "(select *, ROWNUM as rnum" +
                                        " from (select * from Content " +
                                        "  where title like :textToSearch" +
                                        "  and LAST_MODIFICATION_DATE <= :serverTimestamp" +
                                        " ) where ROWNUM <= :endOffset) " +
                                        "where rnum >= :beginOffset")
                        .addQuery("count", "select count(*) as COUNT from Content")
                        .addQuery("insert", "INSERT INTO CONTENT (ID, TITLE, DESCRIPTION, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, CONTENT_TYPE, AUTHOR_REF) " +
                                "VALUES (CONTENT_SEQ.nextval, :title, :description, CURRENT_DATE, CURRENT_DATE, 0, :contentType, :authorId)")
                        .addQuery("updateContentUrl", "UPDATE CONTENT " +
                                "SET FILE_URI = :url, STATUS = 0, LAST_MODIFICATION_DATE = CURRENT_DATE " +
                                "WHERE ID = :id")
                        .addQuery("updateContent", "UPDATE CONTENT " +
                                "SET TITLE = :title, DESCRIPTION = :description, STATUS = 0, LAST_MODIFICATION_DATE = CURRENT_DATE " +
                                "WHERE ID = :id")
                        .addQuery("addLinkWithDomain", "INSERT INTO CONTENT_DOMAIN (CONTENT_REF, DOMAIN_REF) " +
                                "VALUES (:contentId, :domainId)")
                        .addQuery("removeLinkWithDomain", "DELETE FROM CONTENT_DOMAIN " +
                                "WHERE CONTENT_REF = :contentId AND DOMAIN_REF = :domainId");

//        this.contentDetailQueryTemplate = new QueryTemplate<ContentDetail>(dataSource, new ContentDetailRowMapper())
//                        .addQuery("selectById", "select * from Content where id = :id");

        this.idQueryTemplate =
                new QueryTemplate<Long>(dataSource, new LongRowMapper())
                        .addQuery("selectDomainIdsByContentId",
                                "select domain_ref as ID from Content_Domain " +
                                        "where content_ref = :id");
    }

    public ContentDetail getContentDetail(Long id) {

        ContentHeader contentHeader = contentHeaderQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", id)
                        .toContext()
        );

        return new ContentDetail().header(contentHeader).url("/content/content/" + contentHeader.id());
    }

    public List<ContentHeader> getAllContentToRead() {
        List<ContentHeader> entries = new ArrayList<ContentHeader>();
        contentHeaderQueryTemplate.select(entries, "selectAll",
                new QueryExecutionContext().buildParams().toContext()
        );

        return entries;
    }

    public void fetchRecentContents(List<ContentHeader> contentHeaders, int limit) {
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedRecent",
                new QueryExecutionContext().buildParams().integer("limit", limit).toContext()
        );
    }

    public void fetchPopularContent(List<ContentHeader> contentHeaders, int limit) {
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedPopular",
                new QueryExecutionContext().buildParams().integer("limit", limit).toContext()
        );
    }

    public void fetchLastViewedContent(List<ContentHeader> contentHeaders, int limit) {
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedLastViewed",
                new QueryExecutionContext().buildParams().integer("limit", limit).toContext()
        );
    }

    public Count getTotalNumberOfContent() {
        return new Count().count(
                contentHeaderQueryTemplate.selectLong("count",
                        new QueryExecutionContext().buildParams().toContext()).intValue()
        );
    }

    public Long createContent(ContentDetail contentDetail) {
        Map<String, String> genKeys =
                contentHeaderQueryTemplate.insert("insert",
                        new QueryExecutionContext().buildParams()
                                .string("title", contentDetail.header().title())
                                .string("description", contentDetail.header().description())
                                .integer("contentType", contentDetail.header().type().ordinal())
                                .bigint("authorId", 1000) // TODO: change 0 with the current connected user id
                                .toContext(),
                        "id");

        for (Domain domain : contentDetail.header().domains()) {
            contentHeaderQueryTemplate.insert("addLinkWithDomain",
                    new QueryExecutionContext().buildParams()
                            .bigint("contentId", Long.valueOf(genKeys.get("id")))
                            .bigint("domainId", domain.id())
                            .toContext());
        }

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

    public void fetchSearch(List<ContentHeader> contentHeaders, SimpleSearchQueryObject query) {
        Date serverTimestamp;
        String textToSearch;

        if (query.serverTimestamp() == null) {
            serverTimestamp = new DateMidnight().toDate();
        } else {
            serverTimestamp = query.serverTimestamp();
        }

        if (query.query() == null) {
            textToSearch = "";
        } else {
            textToSearch = query.query();
        }

        contentHeaderQueryTemplate.select(contentHeaders, "simpleSearch",
                new QueryExecutionContext()
                        .buildParams()
                        .string("textToSearch", "%" + textToSearch + "%")
                        .integer("beginOffset", query.startingOffset())
                        .integer("endOffset", query.startingOffset() + query.numberOfContents() - 1)
                        .date("serverTimestamp", serverTimestamp)
                        .toContext()
        );
    }

    public String getContentUrl(Long contentId) {
        return contentHeaderQueryTemplate.selectString("selectUrl",
                new QueryExecutionContext()
                        .buildParams()
                        .bigint("id", contentId)
                        .toContext()
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
        return domainDao.getDomains(getDomainIds(contentId));
    }

    private class ContentRowMapper implements RowMapper<ContentHeader> {
        @Override
        public ContentHeader processRow(ResultSet rs) throws SQLException {
            return new ContentHeader()
                    .id(rs.getLong("ID"))
                    .title(rs.getString("TITLE"))
                    .description(rs.getString("DESCRIPTION"))
                    .status(ContentStatus.values()[rs.getInt("STATUS")])
                    .creationDate(rs.getDate("CREATION_DATE"))
                    .lastModificationDate(rs.getDate("LAST_MODIFICATION_DATE"))
                    .type(ContentType.values()[rs.getInt("CONTENT_TYPE")])
                    .author(userDao.getUser(rs.getLong("AUTHOR_REF")))
                    .domains(getDomains(rs.getLong("ID")));
        }
    }
}
