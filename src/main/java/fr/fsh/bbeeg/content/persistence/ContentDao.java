package fr.fsh.bbeeg.content.persistence;


import fr.fsh.bbeeg.common.persistence.ElasticSearchDao;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.content.pojos.*;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.tag.persistence.TagDao;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.pojos.User;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.LongRowMapper;
import jewas.persistence.rowMapper.RowMapper;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author driccio
 */
public class ContentDao {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ContentDao.class);

    private QueryTemplate<ContentHeader> contentHeaderQueryTemplate;
    private QueryTemplate<ContentDetail> contentDetailQueryTemplate;
    private QueryTemplate<Long> idQueryTemplate;
    private UserDao userDao;
    private DomainDao domainDao;
    private TagDao tagDao;
    private ElasticSearchDao esContentDao;

    public ContentDao(DataSource dataSource, UserDao _userDao, DomainDao _domainDao, ElasticSearchDao _esContentDao, TagDao _tagDao) {
        userDao = _userDao;
        domainDao = _domainDao;
        esContentDao = _esContentDao;
        tagDao = _tagDao;

        init(dataSource);
    }

    private void init(DataSource dataSource) {

        // Initializing QueryTemplates
        this.contentHeaderQueryTemplate =
                new QueryTemplate<ContentHeader>(dataSource, new ContentRowMapper())
                        .addQuery("selectById",
                                "select * from CONTENT where ID = :id")
                        .addQuery("selectByUserId",
                                "select * from CONTENT where USER_REF = :userId")
                        .addQuery("selectAll",
                                "select * from CONTENT")
                        .addQuery("selectUrl",
                                "select FILE_URI from CONTENT where ID = :id")
                        .addQuery("selectLimitedRecent",
                                "select * from CONTENT where STATUS = :status " +
                                "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null) " +
                                "order by LAST_MODIFICATION_DATE desc " +
                                "limit :limit")
                        .addQuery("selectLimitedPopular",
                                "select * from CONTENT where STATUS = :status " +
                                "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null) " +
                                "order by POPULARITY desc " +
                                "limit :limit")
                        .addQuery("selectLimitedLastViewed",
                                "select * from CONTENT c where STATUS = :status " +
                                "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null) " +
                                "order by LAST_CONSULTATION_DATE desc " +
                                "limit :limit")
                        .addQuery("selectHigherVersionNumber",
                                "select max(VERSION) from CONTENT where CONTENT_ANCESTOR_REF = :ancestorId")
                        .addQuery("selectLastValidatedVersionContentId",
                                "select ID from CONTENT where CONTENT_ANCESTOR_REF = :ancestorId and STATUS = :status")
                        .addQuery("count",
                                "select count(*) as count from CONTENT where STATUS = :status " +
                                        "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                        "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null)")
                        .addQuery("insert",
                                "insert into CONTENT (TITLE, DESCRIPTION, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, CONTENT_TYPE, AUTHOR_REF, CONTENT_ANCESTOR_REF, VERSION, TAGS) " +
                                "values (:title, :description, :creationDate, :lastModificationDate, 0, :contentType, :authorId, :ancestorId, :version, :tags)")
                        .addQuery("updateAncestorId",
                                "update CONTENT set CONTENT_ANCESTOR_REF = :ancestorId where ID = :id")
                        .addQuery("addLinkWithDomain",
                                "insert into CONTENT_DOMAIN (CONTENT_REF, DOMAIN_REF) values (:contentId, :domainId)")
                        .addQuery("updateContentUrl",
                                "update CONTENT set FILE_URI = :url, LAST_MODIFICATION_DATE = :lastModificationDate where ID = :id")
                        .addQuery("updateContent",
                                "update CONTENT set TITLE = :title, DESCRIPTION = :description, STATUS = 0, LAST_MODIFICATION_DATE = :lastModificationDate, VERSION = :version, TAGS = :tags where ID = :id")
                        .addQuery("updateStatus",
                                "update CONTENT set STATUS = :status, LAST_MODIFICATION_DATE = :lastModificationDate where ID = :id")
                        .addQuery("updatePublicationDates",
                                "update CONTENT set PUBLICATION_START_DATE = :startPublicationDate, PUBLICATION_END_DATE = :endPublicationDate where ID = :id")
                        .addQuery("archiveContent",
                                "update CONTENT set STATUS = :status where ID = :contentId")
                        .addQuery("incrementPopularity",
                                "update CONTENT set POPULARITY = POPULARITY + 1 where ID = :id")
                        .addQuery("updateLastConsultationDate",
                                "update CONTENT set LAST_CONSULTATION_DATE = :lastConsultationDate where ID = :contentId")
                        .addQuery("removeLinkWithDomain",
                                "delete from CONTENT_DOMAIN where CONTENT_REF = :contentId and DOMAIN_REF = :domainId");

       this.contentDetailQueryTemplate =
               new QueryTemplate<ContentDetail>(dataSource, new ContentDetailRowMapper())
                        .addQuery("selectById",
                                "select c.*, cc.id as CONTENT_COMMENT_ID, PUBLICATION_COMMENTS, REJECTION_COMMENTS from CONTENT c left join CONTENT_COMMENT cc on c.ID = cc.CONTENT_REF where c.ID = :id")
                        .addQuery("selectAll",
                                "select c.*, cc.id as CONTENT_COMMENT_ID, PUBLICATION_COMMENTS, REJECTION_COMMENTS from CONTENT c left join CONTENT_COMMENT cc on c.ID = cc.CONTENT_REF")
                        .addQuery("insertPublicationComment",
                                "insert into CONTENT_COMMENT (CONTENT_REF, PUBLICATION_COMMENTS) values (:id, :comment)")
                        .addQuery("insertRejectionComment",
                                "insert into CONTENT_COMMENT (CONTENT_REF, REJECTION_COMMENTS) values (:id, :comment)")
                        .addQuery("updatePublicationComment",
                                "update CONTENT_COMMENT set PUBLICATION_COMMENTS = :comment where CONTENT_REF = :id")
                        .addQuery("updateRejectionComment",
                                "update CONTENT_COMMENT set REJECTION_COMMENTS = :comment where CONTENT_REF = :id");


        this.idQueryTemplate =
                new QueryTemplate<Long>(dataSource, new LongRowMapper())
                        .addQuery("selectDomainIdsByContentId",
                                "select DOMAIN_REF as ID from CONTENT_DOMAIN where CONTENT_REF = :id")
                        .addQuery("selectCommentIdByContentId",
                                "select id from CONTENT_COMMENT where CONTENT_REF = :id");

        // Initializing ES indexes
        String mappingSource = String.format("{ \"%s\" : { \"properties\" : { \"%s\" : { \"type\" : \"attachment\" } } } }",
                esContentDao.indexType(),
                ElasticSearchDao.ES_CONTENT_FIELD_FILECONTENT);
        esContentDao.createIndexIfNotExists(mappingSource);
    }

    /**
     * Return a content detail object fully loaded.
     *
     * @param id the identifier of the content to load
     * @return a {@ContentDetail}
     */
    public ContentDetail getContentDetail(Long id) {
        ContentDetail contentDetail = contentDetailQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", id)
                        .toContext());
        return contentDetail;
    }

    public int getHigherVersionNumber(Long ancestorId) {
        return contentHeaderQueryTemplate.selectLong("selectHigherVersionNumber",
                new QueryExecutionContext().buildParams()
                        .bigint("ancestorId", ancestorId)
                        .toContext()).intValue();

    }

    public Long getLastValidatedVersionContent(Long ancestorId) {
        return contentHeaderQueryTemplate.selectLong("selectLastValidatedVersionContentId",
                new QueryExecutionContext().buildParams()
                        .bigint("ancestorId", ancestorId)
                        .integer("status", ContentStatus.VALIDATED.ordinal())
                        .toContext());
    }
    
     /**
      * Increments by one the number of times this content has been visualized.
      * @param contentId the identifier of the content
      */
     public void incrementPopularity(Long contentId) {
         logger.info("Increment number of views for content id: " + contentId);
         this.contentHeaderQueryTemplate.update("incrementPopularity", new QueryExecutionContext()
                 .buildParams()
                 .bigint("id", contentId)
                 .toContext());
     }

    public void fetchRecentContents(List<ContentHeader> contentHeaders, int limit) {
        Date today = new DateMidnight().toDate();
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedRecent",
                new QueryExecutionContext()
                        .buildParams()
                        .integer("status", ContentStatus.VALIDATED.ordinal())
                        .date("today", today)
                        .integer("limit", limit)
                        .toContext()
        );
    }

    /**
     * Fetches the most popular contents which have been validated.
     * @param contentHeaders the result list.
     * @param limit the number of contents to fetch.
     */
    public void fetchPopularContent(List<ContentHeader> contentHeaders, int limit) {
        Date today = new DateMidnight().toDate();
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedPopular",
                new QueryExecutionContext()
                        .buildParams()
                        .integer("status", ContentStatus.VALIDATED.ordinal())
                        .date("today", today)
                        .integer("limit", limit)
                        .toContext()
        );
    }

    public void fetchLastViewedContent(List<ContentHeader> contentHeaders, int limit) {
        Date today = new DateMidnight().toDate();
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedLastViewed",
                new QueryExecutionContext()
                        .buildParams()
                        .integer("status", ContentStatus.VALIDATED.ordinal())
                        .date("today", today)
                        .integer("limit", limit)
                        .toContext()
        );
    }

    public Count getTotalNumberOfContent() {
        Date today = new DateMidnight().toDate();
        return new Count().count(
                contentHeaderQueryTemplate.selectLong("count",
                        new QueryExecutionContext()
                                .buildParams()
                                .date("today", today)
                                .integer("status", ContentStatus.VALIDATED.ordinal())
                                .toContext()
                ).intValue()
        );
    }

    public Long createContent(ContentDetail contentDetail) {
        Date currentDate = new DateMidnight().toDate();

        contentDetail.header().creationDate(currentDate).lastModificationDate(currentDate);

        Map<String, String> genKeys =
                contentHeaderQueryTemplate.insert("insert",
                        new QueryExecutionContext().buildParams()
                                .string("title", contentDetail.header().title())
                                .string("description", contentDetail.header().description())
                                .string("tags", listTagsToString(contentDetail.header().tags()))
                                .integer("contentType", contentDetail.header().type().ordinal())
                                .bigint("authorId", contentDetail.header().author().id())
                                .integer("status", ContentStatus.DRAFT.ordinal())
                                .date("creationDate", contentDetail.header().creationDate())
                                .date("lastModificationDate",
                                        contentDetail.header().lastModificationDate())
                                .bigint("ancestorId", contentDetail.header().ancestorId())
                                .bigint("version", contentDetail.header().version())
                                .toContext(),
                        "id");
        contentDetail.header().id(Long.parseLong(genKeys.get("id")));
        if (contentDetail.header().ancestorId() == null) {
            contentHeaderQueryTemplate.update("updateAncestorId",
                    new QueryExecutionContext().buildParams()
                            .bigint("ancestorId", contentDetail.header().id())
                            .bigint("id", contentDetail.header().id())
                            .toContext());
        }

        Collection<Domain> domains = contentDetail.header().domains();
        if (domains != null) {
            for (Domain domain : domains) {
                contentHeaderQueryTemplate.insert("addLinkWithDomain",
                        new QueryExecutionContext().buildParams()
                                .bigint("contentId", Long.valueOf(genKeys.get("id")))
                                .bigint("domainId", domain.id())
                                .toContext());
            }
        }

        // Update TAGS table
        Collection<String> tags = contentDetail.header().tags();
        if (tags != null) {
            for (String tag : tags) {
                tagDao.createOrUpdateTag(tag);
            }
        }
        ContentDetail cd = getContentDetail(contentDetail.header().id());
        if (cd != null) {
            try {
                esContentDao.insertContentInElasticSearch(cd);
            } catch (IOException e) {
                logger.error("Failed to insert content in elastic search", e);
            }
        }
        return Long.valueOf(genKeys.get("id"));
    }

    public void reIndexAllInElasticSearch() {
        List<ContentDetail> contents = new ArrayList<ContentDetail>();
        contentDetailQueryTemplate.select(contents, "selectAll",
                new QueryExecutionContext().buildParams().toContext());
        logger.info("Start re-index contents from database into elastic search...");
        for(ContentDetail contentDetail : contents) {
            try {
                esContentDao.insertContentInElasticSearch(contentDetail);
            } catch (IOException e) {
                logger.error("Failed to insert content into elastic search. Content Id: " + contentDetail.header().id(), e);
            }
        }
        logger.info("Re-index contents operation completed");
    }

    public void updateContentUrl(Long contentId, String url) {
        Date currentDate = new DateMidnight().toDate();
        contentHeaderQueryTemplate.update("updateContentUrl",
                new QueryExecutionContext().buildParams()
                        .string("url", url)
                        .bigint("id", contentId)
                        .date("lastModificationDate", currentDate)
                        .toContext());
    }

    public void updateContentOfContent(Long contentId, String url) {
        
        updateContentUrl(contentId, url);

        ContentDetail cd = getContentDetail(contentId);
        if (cd != null) {
            try {
                esContentDao.insertContentInElasticSearch(cd);
            } catch (IOException e) {
                logger.error("Failed to insert content in elastic search for content: %s", contentId, e);
            }
        }
    }

    public void updateContent(ContentDetail contentDetail) {
        Date currentDate = new DateMidnight().toDate();

        /* Update the content in the DB */
        List<String> tags = contentDetail.header().tags();
        Long contentId = contentDetail.header().id();
        updateContentTags(contentDetail);
        contentHeaderQueryTemplate.update("updateContent",
                new QueryExecutionContext().buildParams()
                        .string("title", contentDetail.header().title())
                        .string("description", contentDetail.header().description())
                        .string("tags", listTagsToString(tags))
                        .bigint("id", contentId)
                        .date("lastModificationDate", currentDate)
                        .bigint("version", contentDetail.header().version())
                        .toContext());

        updateContentDomains(contentDetail);

        // Insert into ES the content.
        ContentDetail cd = getContentDetail(contentId);
        if (cd != null) {
            try {
                esContentDao.insertContentInElasticSearch(cd);
            } catch (IOException e) {
                logger.error("Failed to insert content in elastic search for content: {}", contentId, e);
            }
        }
    }

    private void updateContentTags(ContentDetail contentDetail) {
        List<String> tags = contentDetail.header().tags();
        ContentDetail fromDB = getContentDetail(contentDetail.header().id());

        List<String> persistedTags = null;
        // Check tags
        if (fromDB == null) {
            persistedTags = Collections.emptyList();
        } else {
            persistedTags = fromDB.header().tags();
        }

        if (tags != null) {
            for (String tag : tags) {
                if (!persistedTags.contains(tag)) {
                    tagDao.createOrUpdateTag(tag);
                }
            }
        }
        for (String tag : persistedTags) {
            if (tags == null || !tags.contains(tag)) {
                tagDao.deleteOrUpdateTag(tag);
            }
        }
    }

    /**
     * Updates the domains linked to the updated content.
     *
     * @param contentDetail the content being updated
     */
    private void updateContentDomains(ContentDetail contentDetail) {
        Long contentId = contentDetail.header().id();
        // Get current domain ids that are linked with the content.
        List<Long> domainsIds = getDomainIds(contentId);

        // Check added domains
        for (Domain domainToCheck : contentDetail.header().domains()) {
            if (!domainsIds.contains(domainToCheck.id())) {
                // Add new link with domains
                contentHeaderQueryTemplate.insert("addLinkWithDomain",
                        new QueryExecutionContext().buildParams()
                                .bigint("contentId", contentId)
                                .bigint("domainId", domainToCheck.id())
                                .toContext());
            }
        }

        // Check domains to remove
        for (Long domainIdToCheckForRemove : domainsIds) {
            boolean found = false;

            for (Domain domain : contentDetail.header().domains()) {
                if (domainIdToCheckForRemove.equals(domain.id())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Remove link with domains
                contentHeaderQueryTemplate.delete("removeLinkWithDomain",
                        new QueryExecutionContext().buildParams()
                                .bigint("contentId", contentId)
                                .bigint("domainId", domainIdToCheckForRemove)
                                .toContext());
            }
        }
    }

    private void fetchByIds(List<ContentHeader> contentHeaders, List<Long> contentIds) {
        // Fetch the contents from the database via the content ids.
        if (contentIds != null && !contentIds.isEmpty()) {
            for (Long contentId : contentIds) {
                    ContentHeader header = contentHeaderQueryTemplate.selectObject("selectById",
                            new QueryExecutionContext().buildParams()
                                    .bigint("id", contentId)
                                    .toContext());
                // TODO: Remove that test by tuning elastic search to not retrieve that status.
                if (header != null && !ContentStatus.ARCHIVED.equals(header.status())) {
                    contentHeaders.add(header);
                }
            }
        }
    }

    public void fetchSearch(List<ContentHeader> contentHeaders, SimpleSearchQueryObject query) {
        fetchByIds(contentHeaders, esContentDao.search(query));
    }

    public void fetchSearch(List<ContentHeader> contentHeaders, AdvancedSearchQueryObject query) {
        fetchByIds(contentHeaders, esContentDao.search(query));
   }



    public String getContentUrl(Long contentId) {
        return contentHeaderQueryTemplate.selectString("selectUrl",
                new QueryExecutionContext()
                        .buildParams()
                        .bigint("id", contentId)
                        .toContext()
        );
    }

    public void fetchContents(List<ContentHeader> contentHeaders, User user) {
        contentHeaderQueryTemplate.select(contentHeaders, "selectByUserId",
                new QueryExecutionContext()
                        .buildParams()
                        .bigint("userId", user.id())
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

    private List<String> stringTagsToList(String tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(";"));
    }

    private String listTagsToString(List<String> tags) {
        if (tags == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for(String tag : tags) {
                sb.append(tag);
                sb.append(";");
            }
        sb.deleteCharAt(sb.lastIndexOf(";"));
        return sb.toString();
        }
    }

    /**
     * Update the content status.
     *
     * @param contentId the content to update.
     * @param newStatus the new status to apply to the content.
     */
    public void updateContentStatus(Long contentId, ContentStatus newStatus) {
        Date currentDate = new DateMidnight().toDate();

        logger.info("Update status of content: {} to {}", contentId, newStatus);
        contentHeaderQueryTemplate.update("updateStatus",
                new QueryExecutionContext()
                        .buildParams()
                        .bigint("id", contentId)
                        .integer("status", newStatus.ordinal())
                        .date("lastModificationDate", currentDate)
                        .toContext()
        );

        ContentDetail cd = getContentDetail(contentId);
        if (cd != null) {
            try {
                esContentDao.insertContentInElasticSearch(cd);
            } catch (IOException e) {
                logger.error("Cannot insert content into elastic search for content id:" + contentId, e);
            }
        }
    }
    
    /**
     * Update publication dates.
     * @param contentId the referenced content being updated.
     * @param publicationDetails the publication detail information.
     */
    public void updateContentPublicationDates(Long contentId, ContentPublicationDetail publicationDetails) {
        logger.info("Update publication dates for content %s.", contentId + " Start: " + publicationDetails.start() + " End: " + publicationDetails.end());
        contentHeaderQueryTemplate.update("updatePublicationDates", new QueryExecutionContext()
                .buildParams()
                .date("startPublicationDate", publicationDetails.start())
                .date("endPublicationDate", publicationDetails.end())
                .bigint("id", contentId)
                .toContext());
    }

    /**
     * Update publication comments.
     *
     * @param contentId          the referenced content being updated.
     * @param newStatus          the content new status.
     * @param publicationDetails the publication detail information.
     */
    public void updateContentPublicationComments(Long contentId, ContentStatus newStatus, ContentPublicationDetail publicationDetails) {
        Long commentId = idQueryTemplate.selectLong("selectCommentIdByContentId",
                new QueryExecutionContext().buildParams()
                        .bigint("id", contentId)
                        .toContext()
        );

        if (ContentStatus.TO_BE_VALIDATED.equals(newStatus)) {
            if (commentId == null) {
                logger.info("Add publication comment to content: " + contentId);
                contentDetailQueryTemplate.insert("insertPublicationComment",
                        new QueryExecutionContext()
                                .buildParams()
                                .bigint("id", contentId)
                                .string("comment", publicationDetails.comments())
                                .toContext(), "id");
            } else {
                logger.info("Update publication comment to content: " + contentId);
                contentDetailQueryTemplate.update("updatePublicationComment",
                        new QueryExecutionContext()
                                .buildParams()
                                .bigint("id", contentId)
                                .string("comment", publicationDetails.comments())
                                .toContext());
            }
        } else if (ContentStatus.REJECTED.equals(newStatus)) {
            if (commentId == null) {
                logger.info("Add rejection comment to content: " + contentId);
                contentDetailQueryTemplate.insert("insertRejectionComment",
                        new QueryExecutionContext()
                                .buildParams()
                                .bigint("id", contentId)
                                .string("comment", publicationDetails.comments())
                                .toContext(), "id");
            } else {
                logger.info("Update rejection comment to content: " + contentId);
                contentDetailQueryTemplate.update("updateRejectionComment",
                        new QueryExecutionContext()
                                .buildParams()
                                .bigint("id", contentId)
                                .string("comment", publicationDetails.comments())
                                .toContext());
            }
        } else {
            logger.error("Should not update comments for given content new status: %s", newStatus);
        }
    }

    /**
     * Archive the content represented by the given content identifier.
     *
     * @param contentId the identifier of the content to archive
     */
    public void archiveContent(Long contentId) {
        contentHeaderQueryTemplate.update("archiveContent",
                new QueryExecutionContext().buildParams()
                        .bigint("contentId", contentId)
                        .bigint("status", ContentStatus.ARCHIVED.ordinal())
                        .toContext());
    }

    /**
     * Update the last consultation date stored on the given content.
     *
     * @param contentId the content identifier
     */
    public void updateLastConsultationDate(Long contentId, Date date) {
        logger.info("Update the last consultation date for content: {}", contentId);
        contentHeaderQueryTemplate.update("updateLastConsultationDate",
                new QueryExecutionContext().buildParams()
                        .bigint("contentId", contentId)
                        .date("lastConsultationDate", date)
                        .toContext());
    }

    public void selectAll(List<ContentDetail> contents) {
        contentDetailQueryTemplate.select(contents, "selectAll",
                new QueryExecutionContext().buildParams().toContext());
    }

    private class ContentRowMapper implements RowMapper<ContentHeader> {
        @Override
        public ContentHeader processRow(ResultSet rs) throws SQLException {
            return new ContentHeader()
                    .id(rs.getLong("ID"))
                    .title(rs.getString("TITLE"))
                    .version(rs.getInt("VERSION"))
                    .description(rs.getString("DESCRIPTION"))
                    .status(ContentStatus.values()[rs.getInt("STATUS")])
                    .ancestorId(rs.getLong("CONTENT_ANCESTOR_REF"))
                    .creationDate(rs.getDate("CREATION_DATE"))
                    .lastModificationDate(rs.getDate("LAST_MODIFICATION_DATE"))
                    .startPublicationDate(rs.getDate("PUBLICATION_START_DATE"))
                    .endPublicationDate(rs.getDate("PUBLICATION_END_DATE"))
                    .type(ContentType.values()[rs.getInt("CONTENT_TYPE")])
                    .popularity(rs.getLong("POPULARITY"))
                    .author(userDao.getUser(rs.getLong("AUTHOR_REF")))
                    .domains(getDomains(rs.getLong("ID")))
                    .tags(stringTagsToList(rs.getString("TAGS")));
        }
    }

    private class ContentDetailRowMapper implements RowMapper<ContentDetail> {
        private ContentRowMapper contentRowMapper = new ContentRowMapper();

        @Override
        public ContentDetail processRow(ResultSet resultSet) throws SQLException {
            ContentHeader contentHeader = contentRowMapper.processRow(resultSet);

            ContentDetail contentDetail = new ContentDetail();
            contentDetail.header(contentHeader)
                    .publicationComments(resultSet.getString("PUBLICATION_COMMENTS"))
                    .rejectionComments(resultSet.getString("REJECTION_COMMENTS"))
                    .url(resultSet.getString("FILE_URI"));
            return contentDetail;
        }
    }
}
