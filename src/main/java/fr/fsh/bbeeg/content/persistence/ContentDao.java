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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static fr.fsh.bbeeg.content.pojos.SearchMode.values;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author driccio
 */
public class ContentDao {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ContentDao.class);

    private Client client;
    private QueryTemplate<ContentHeader> contentHeaderQueryTemplate;
    private QueryTemplate<ContentDetail> contentDetailQueryTemplate;
    private QueryTemplate<Long> idQueryTemplate;
    private UserDao userDao;
    private DomainDao domainDao;
    private TagDao tagDao;
    private ElasticSearchDao esContentDao;

    public ContentDao(DataSource dataSource, Client _client, UserDao _userDao, DomainDao _domainDao, ElasticSearchDao _esContentDao, TagDao _tagDao) {
        client = _client;
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
                                "select FILE_URI from Content where id = :id")
                        .addQuery("selectLimitedRecent",
                                "select * from " +
                                        "(select * from CONTENT where STATUS = :status " +
                                        "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                        "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null) " +
                                        "order by ID desc) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectLimitedPopular",
                                "select * from " +
                                        "(select * from CONTENT where STATUS = :status " +
                                        "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                        "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null) " +
                                        "order by POPULARITY desc) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectLimitedLastViewed",
                                "select * from " +
                                        "(select * from CONTENT where STATUS = :status " +
                                        "and (PUBLICATION_START_DATE <= :today or PUBLICATION_START_DATE is null) " +
                                        "and (:today <= PUBLICATION_END_DATE or PUBLICATION_END_DATE is null) " +
                                        "order by LAST_CONSULTATION_DATE desc) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectHigherVersionNumber",
                                "select max(VERSION) from CONTENT where CONTENT_ANCESTOR_REF = :ancestorId")
                        .addQuery("count",
                                "select count(*) as count from CONTENT where STATUS = :status")
                        .addQuery("insert",
                                "insert into CONTENT (ID, TITLE, DESCRIPTION, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, CONTENT_TYPE, AUTHOR_REF, CONTENT_ANCESTOR_REF, VERSION, TAGS) " +
                                "values (CONTENT_SEQ.nextval, :title, :description, :creationDate, :lastModificationDate, 0, :contentType, :authorId, case when :ancestorId is null then (select CONTENT_SEQ.currVal) else :ancestorId end, :version, :tags)")
                        .addQuery("addLinkWithDomain",
                                "insert into CONTENT_DOMAIN (CONTENT_REF, DOMAIN_REF) values (:contentId, :domainId)")
                        .addQuery("updateContentUrl",
                                "update content set FILE_URI = :url, LAST_MODIFICATION_DATE = :lastModificationDate where ID = :id")
                        .addQuery("updateContent",
                                "update CONTENT set TITLE = :title, DESCRIPTION = :description, STATUS = 0, LAST_MODIFICATION_DATE = :lastModificationDate, VERSION = :version, TAGS = :tags where ID = :id")
                        .addQuery("updateStatus",
                                "update CONTENT set STATUS = :status, LAST_MODIFICATION_DATE = :lastModificationDate where ID = :id")
                        .addQuery("updatePublicationDates",
                                "update CONTENT set PUBLICATION_START_DATE = :startPublicationDate, PUBLICATION_END_DATE = :endPublicationDate where ID = :id")
                        .addQuery("archiveLastValidatedVersion",
                                "update CONTENT set STATUS = :status where VERSION = " +
                                    "(select max(VERSION) from CONTENT where CONTENT_ANCESTOR_REF = :ancestorId and STATUS = " + ContentStatus.VALIDATED.ordinal() + ")")
                        .addQuery("incrementPopularity",
                                "update CONTENT set POPULARITY = POPULARITY + 1 where ID = :id and STATUS in :statuses")
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
                                "insert into CONTENT_COMMENT (ID, CONTENT_REF, PUBLICATION_COMMENTS) values (CONTENT_COMMENT_SEQ.nextval, :id, :comment)")
                        .addQuery("insertRejectionComment",
                                "insert into CONTENT_COMMENT (ID, CONTENT_REF, REJECTION_COMMENTS) values (CONTENT_COMMENT_SEQ.nextval, :id, :comment)")
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

    public int getHigherVersionNumber(Long ancestorId) {
        return contentHeaderQueryTemplate.selectLong("selectHigherVersionNumber",
                new QueryExecutionContext().buildParams()
                        .bigint("ancestorId", ancestorId)
                        .toContext()).intValue();

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
                 .array("statuses", ContentStatus.VALIDATED.ordinal())
                 .toContext());
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
        return new Count().count(
                contentHeaderQueryTemplate.selectLong("count",
                        new QueryExecutionContext()
                                .buildParams()
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

        // Check tags

        List<String> persistedTags = fromDB.header().tags();

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
        if (!contentIds.isEmpty()) {
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
        Date serverTimestamp; // TODO: Don't forget to filter by server timestamp

        if (query.serverTimestamp() == null) {
            serverTimestamp = new DateMidnight().toDate();
        } else {
            serverTimestamp = query.serverTimestamp();
        }

        List<Integer> statuses = filterContentStatuses(values()[query.searchMode()]);

        BoolQueryBuilder elasticSearchQuery = createElasticSearchQuery(statuses);
        configureFullTextQuery(elasticSearchQuery, query.query());
        configureAuthorsQuery(elasticSearchQuery, query.authors());

        FilterBuilder filterBuilder = null;
        if (SearchMode.ALL_VALIDATED.ordinal() == query.searchMode()) {
            filterBuilder = configurePublicationDateFilter();
        }

        List<Long> contentIds;
        if (filterBuilder == null) {
            contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), elasticSearchQuery);
        } else {
            QueryBuilder filteredQuery = QueryBuilders.filteredQuery(elasticSearchQuery, filterBuilder);
            contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), filteredQuery);
        }
        fetchByIds(contentHeaders, contentIds);
    }

    public void fetchSearch(List<ContentHeader> contentHeaders, AdvancedSearchQueryObject query) {
        Date serverTimestamp; // TODO: Don't forget to filter by server timestamp

        if (query.serverTimestamp() == null) {
            serverTimestamp = new DateMidnight().toDate();
        } else {
            serverTimestamp = query.serverTimestamp();
        }

        List<Integer> statuses = filterContentStatuses(values()[query.searchMode()]);

        BoolQueryBuilder elasticSearchQuery = createElasticSearchQuery(statuses);
        configureFullTextQuery(elasticSearchQuery, query.query());
        configureAuthorsQuery(elasticSearchQuery, query.authors());
        configureDomainsQuery(elasticSearchQuery, query.domains());
        configureContentTypeQuery(elasticSearchQuery, query.searchTypes());


        FilterBuilder filterBuilders = getAdvancedQueryFilterBuilders(query);

        List<Long> contentIds;
        if (filterBuilders == null) {
            contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), elasticSearchQuery);
        } else {
            QueryBuilder filteredQuery = QueryBuilders.filteredQuery(elasticSearchQuery, filterBuilders);
            contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), filteredQuery);
        }
        fetchByIds(contentHeaders, contentIds);
   }

    /**
     * Build the dates filters with creation date filter and publication start and end dates filter for ALL_VALIDATED search mode
     *
     * @param query the query to performed
     * @return a {@FilterBuilder}
     */
    private FilterBuilder getAdvancedQueryFilterBuilders(AdvancedSearchQueryObject query) {
        FilterBuilder filterBuilder = null;
        RangeFilterBuilder creationDateRangeFilter = configureCreationDateFilter(query.from(), query.to());
        if (creationDateRangeFilter != null) {
            filterBuilder = FilterBuilders.andFilter(creationDateRangeFilter);
        }
        if (SearchMode.ALL_VALIDATED.ordinal() == query.searchMode()) {
            filterBuilder = FilterBuilders.andFilter(configurePublicationDateFilter());
        }
        return filterBuilder;
    }

    /**
     * Build the publication start and end date elastic search range filter.
     *
     * @return a {@FilterBuilder}
     */
    private FilterBuilder configurePublicationDateFilter() {
        Date today = new DateMidnight().toDate();

        RangeFilterBuilder startPublicationRangeFilter = FilterBuilders.rangeFilter(ElasticSearchDao.ES_CONTENT_FIELD_PUBLICATION_START_DATE);
        startPublicationRangeFilter.lte(today);

        MissingFilterBuilder startPublicationMissing = FilterBuilders.missingFilter(ElasticSearchDao.ES_CONTENT_FIELD_PUBLICATION_START_DATE);

        RangeFilterBuilder endPublicationRangeFilter = FilterBuilders.rangeFilter(ElasticSearchDao.ES_CONTENT_FIELD_PUBLICATION_END_DATE);
        endPublicationRangeFilter.gte(today);

        MissingFilterBuilder endPublicationMissing = FilterBuilders.missingFilter(ElasticSearchDao.ES_CONTENT_FIELD_PUBLICATION_END_DATE);

        FilterBuilder startPublicationDateFilterBuilder = FilterBuilders.orFilter(startPublicationRangeFilter, startPublicationMissing);
        FilterBuilder endPublicationDateFilterBuilder = FilterBuilders.orFilter(endPublicationRangeFilter, endPublicationMissing);
        return FilterBuilders.andFilter(startPublicationDateFilterBuilder, endPublicationDateFilterBuilder);
    }

    private void configureAuthorsQuery(BoolQueryBuilder elasticSearchQuery, String[] authors) {
        if (authors != null && authors.length > 0) {
            elasticSearchQuery.must(QueryBuilders.termsQuery(ElasticSearchDao.ES_CONTENT_FIELD_AUTHOR, authors));
        }
    }

    private BoolQueryBuilder createElasticSearchQuery(List<Integer> statuses) {
        return boolQuery().must(inQuery(ElasticSearchDao.ES_CONTENT_FIELD_STATUS, statuses.toArray()));
        //.must(QueryBuilders.rangeQuery(ES_CONTENT_LAST_MODIF_DATE).lt(serverTimestamp));
        // TODO: Use serverTImeStamp to filter
    }

    /**
     * Search in elastic search and returns the relational identifiers of the hits.
     *
     * @param startingOffset     the starting offset used to set the from property of the elastic search query
     * @param numberOfContents   the limit of results to return
     * @param elasticSearchQuery the elastic search
     * @return a list of ids of matched contents in elastic search
     */
    private List<Long> searchInElasticSearch(Integer startingOffset, Integer numberOfContents, QueryBuilder elasticSearchQuery) {
        SearchResponse sResponse = client.prepareSearch(esContentDao.indexName())
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(elasticSearchQuery)
                .setFrom(startingOffset)
                .setSize(numberOfContents)
                .addSort(ElasticSearchDao.ES_CONTENT_FIELD_LAST_MODIF_DATE, SortOrder.DESC)
                .addSort("_score", SortOrder.DESC)
                        //.setMinScore(0.3f)
                .execute().actionGet();

        // Get the content ids from the result.
        List<Long> contentIds = new ArrayList<Long>();

        for (SearchHit searchHit : sResponse.getHits()) {
            contentIds.add(Long.parseLong(searchHit.id()));
        }
        return contentIds;
    }

    /**
     * Returns a RangeFilterBuilder depending on the given range dates. If both dates are <code>null</code>, return <code>null</code>.
     * @param from the from date filter
     * @param to   the to date filter
     */
    private RangeFilterBuilder configureCreationDateFilter(Date from, Date to) {
        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(ElasticSearchDao.ES_CONTENT_FIELD_CREATION_DATE);
        if (from != null) {
            rangeFilter.from(from);
        }
        if (to != null) {
            rangeFilter.to(to);
        }
        if (from != null || to != null) {
            return rangeFilter;
        } else {
            return null;
        }
    }

    /**
     * Filter the content by status depending on the search mode.
     * @param searchMode the current mode of the search. Must not be null.
     * @return the list of statuses that will be accepted by the search.
     */
    private List<Integer> filterContentStatuses(SearchMode searchMode) {

        if (searchMode.ordinal() >= values().length) {
            // TODO: throw an exception
        }

        List<Integer> statuses = new ArrayList<Integer>();

        switch (searchMode) {
            case ALL_VALIDATED:
                statuses.add(ContentStatus.VALIDATED.ordinal());
                break;
            case ONLY_USER_CONTENTS :
                statuses.add(ContentStatus.DRAFT.ordinal());
                statuses.add(ContentStatus.VALIDATED.ordinal());
                statuses.add(ContentStatus.REJECTED.ordinal());
                statuses.add(ContentStatus.TO_BE_VALIDATED.ordinal());
                statuses.add(ContentStatus.TO_BE_DELETED.ordinal());
                break;
            case ONLY_CONTENTS_TO_TREAT:
                statuses.add(ContentStatus.TO_BE_VALIDATED.ordinal());
                statuses.add(ContentStatus.TO_BE_DELETED.ordinal());
                break;
            default:
                // TODO
        }
        return statuses;
    }

    /**
     * Contribute to the elastic search query adding full text search on 'title', 'description', 'fileContent' and 'tags'.
     * @param elasticSearchQuery the elastic search query to contribute to.
     * @param textToSearch the text to be searched
     */
    private void configureFullTextQuery(BoolQueryBuilder elasticSearchQuery,String textToSearch) {
        if (textToSearch != null && !textToSearch.isEmpty()) {
            elasticSearchQuery.must(QueryBuilders.disMaxQuery()
                    .add(termQuery(ElasticSearchDao.ES_CONTENT_FIELD_TITLE, textToSearch).boost(5))
                    .add(termQuery(ElasticSearchDao.ES_CONTENT_FIELD_DESCRIPTION, textToSearch).boost(3))
                    .add(termQuery(ElasticSearchDao.ES_CONTENT_FIELD_FILECONTENT, textToSearch).boost(4))
                    .add(termQuery(ElasticSearchDao.ES_CONTENT_FIELD_TAGS, textToSearch).boost(5)));
        }
    }
    
    /**
     * Contribute to the elastic search query adding search criteria on 'domains'.
     * The match is made on the intersection of the list of searched domains and found contents domain list.
     *
     * @param elasticSearchQuery the elastic search query to contribute to.
     * @param domains            the domains used to filter the returned contents
     */
    private void configureDomainsQuery(BoolQueryBuilder elasticSearchQuery, String[] domains) {
        if (domains != null && domains.length > 0) {
            elasticSearchQuery.must(QueryBuilders.termsQuery(ElasticSearchDao.ES_CONTENT_FIELD_DOMAINS, domains));
        }
    }
    
    /**
     * Contribute to the elastic search query adding search criteria on 'contentType'.
     *
     * @param elasticSearchQuery the elastic search query to contribute to.
     * @param searchTypes        the content types used to filter the returned contents
     */
    private void configureContentTypeQuery(BoolQueryBuilder elasticSearchQuery, String[] searchTypes) {
        if (searchTypes != null && searchTypes.length > 0) {
            elasticSearchQuery.must(QueryBuilders.termsQuery(ElasticSearchDao.ES_CONTENT_FIELD_CONTENT_TYPE, searchTypes));
        }
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
     * Find the previous version of the given content reference which was VALIDATED and changes its status to ARCHIVED.
     *
     * @param commonAncestorId the common reference to the base version of the content to archive
     */
    public void archivePreviousVersion(Long commonAncestorId) {
        logger.info("Archive previous status for content reference: %s", commonAncestorId.toString());
        contentHeaderQueryTemplate.update("archiveLastValidatedVersion",
                new QueryExecutionContext().buildParams()
                        .bigint("ancestorId", commonAncestorId)
                        .bigint("status", ContentStatus.ARCHIVED.ordinal())
                        .toContext());
    }

    /**
     * Update the last consultation date stored on the given content.
     *
     * @param contentId the content identifier
     */
    public void updateLastConsultationDate(Long contentId, Date date) {
         logger.info("Update the last consultation date for content: " + contentId);
        contentHeaderQueryTemplate.update("updateLastConsultationDate",
                new QueryExecutionContext().buildParams()
                        .bigint("contentId", contentId)
                        .date("lastConsultationDate", date)
                        .toContext());
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
