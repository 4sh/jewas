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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateMidnight;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static fr.fsh.bbeeg.content.pojos.SearchMode.values;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author driccio
 */
public class ContentDao {
    private Client client;
    private QueryTemplate<ContentHeader> contentHeaderQueryTemplate;
    private QueryTemplate<ContentDetail> contentDetailQueryTemplate;
     private QueryTemplate<Long> idQueryTemplate;
    private UserDao userDao;
    private DomainDao domainDao;
    private TagDao tagDao;
    private ElasticSearchDao esContentDao;

    private static final String ES_CONTENT_FIELD_FILECONTENT = "fileContent";
    private static final String ES_CONTENT_FIELD_AUTHOR = "author";
    private static final String ES_CONTENT_FIELD_TITLE = "title";
    private static final String ES_CONTENT_FIELD_DESCRIPTION = "description";
    private static final String ES_CONTENT_FIELD_CONTENT_TYPE = "contentType";
    private static final String ES_CONTENT_FIELD_CREATION_DATE = "creationDate";
    private static final String ES_CONTENT_FIELD_LAST_MODIF_DATE = "lastModificationDate";
    private static final String ES_CONTENT_FIELD_DOMAINS = "domains";
    private static final String ES_CONTENT_FIELD_TAGS = "tags";
    private static final String ES_CONTENT_FIELD_STATUS = "status";
    private static final String ES_CONTENT_FIELD_ANCESTOR = "ancestor";


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
                        .addQuery("selectById", "select * from Content where id = :id")
                        .addQuery("selectByIds", "select * from Content where id in :ids")
                        .addQuery("selectByUserId", "select * from Content where user_ref = :userId")
                        .addQuery("selectAll", "select * from Content")
                        .addQuery("selectUrl", "select FILE_URI from Content where id = :id")
                        .addQuery("selectLimitedRecent",
                                "select * from " +
                                        "(select * from Content where status = :status " +
                                        "order by id desc) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectLimitedPopular", // TODO: change request or remove it. Use elasticSearch insteed
                                "select * from " +
                                        "(select * from Content where status = :status) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("selectLimitedLastViewed", // TODO: change request or remove it. Use elasticSearch insteed
                                "select * from " +
                                        "(select * from Content where status = :status) " +
                                        "where ROWNUM <= :limit")
                        .addQuery("simpleSearch",
                                "select * from " +
                                        "(select *, ROWNUM as rnum" +
                                        " from (select * from Content " +
                                        "  where title like :textToSearch" +
                                        "  and LAST_MODIFICATION_DATE <= :serverTimestamp" +
                                        "  and status in :statuses" +
                                        "  and (:userId IS NULL or AUTHOR_REF = :userId)" +
                                        " ) where ROWNUM <= :endOffset) " +
                                        "where rnum >= :beginOffset")
                        .addQuery("count", "select count(*) as COUNT from Content where status = :status")
                        .addQuery("insert", "INSERT INTO CONTENT (ID, TITLE, DESCRIPTION, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, CONTENT_TYPE, AUTHOR_REF, CONTENT_ANCESTOR_REF, TAGS) " +
                                "VALUES (CONTENT_SEQ.nextval, :title, :description, :creationDate, :lastModificationDate, 0, :contentType, :authorId, :ancestorId, :tags)")
                        .addQuery("updateContentUrl", "UPDATE CONTENT " +
                                "SET FILE_URI = :url, STATUS = 0, LAST_MODIFICATION_DATE = :lastModificationDate " +
                                "WHERE ID = :id")
                        .addQuery("updateContent", "UPDATE CONTENT " +
                                "SET TITLE = :title, DESCRIPTION = :description, STATUS = 0, LAST_MODIFICATION_DATE = :lastModificationDate, TAGS = :tags " +
                                "WHERE ID = :id")
                        .addQuery("addLinkWithDomain", "INSERT INTO CONTENT_DOMAIN (CONTENT_REF, DOMAIN_REF) " +
                                "VALUES (:contentId, :domainId)")
                        .addQuery("removeLinkWithDomain", "DELETE FROM CONTENT_DOMAIN " +
                                "WHERE CONTENT_REF = :contentId AND DOMAIN_REF = :domainId")
                        .addQuery("updateStatus", "UPDATE CONTENT " +
                                "SET STATUS = :status, LAST_MODIFICATION_DATE = :lastModificationDate "+
                                "WHERE ID = :id")
                        .addQuery("addComment", "INSERT INTO CONTENT_COMMENT (ID, CONTENT_REF, COMMENT) VALUES (CONTENT_COMMENT_SEQ.nextval, :id, :comment)");

       this.contentDetailQueryTemplate =
               new QueryTemplate<ContentDetail>(dataSource, new ContentDetailRowMapper())
                        .addQuery("selectById", "select * from Content where id = :id")
                        .addQuery("selectAll", "select * from Content");

        this.idQueryTemplate =
                new QueryTemplate<Long>(dataSource, new LongRowMapper())
                        .addQuery("selectDomainIdsByContentId",
                                "select domain_ref as ID from Content_Domain " +
                                        "where content_ref = :id");

        // Initializing ES indexes
        String mappingSource = String.format("{ \"%s\" : { \"properties\" : { \"%s\" : { \"type\" : \"attachment\" } } } }",
                esContentDao.indexType(),
                ES_CONTENT_FIELD_FILECONTENT);
        esContentDao.createIndexIfNotExists(mappingSource);
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
                new QueryExecutionContext()
                        .buildParams()
                        .integer("status", ContentStatus.VALIDATED.ordinal())
                        .integer("limit", limit)
                        .toContext()
        );
    }

    public void fetchPopularContent(List<ContentHeader> contentHeaders, int limit) {
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedPopular",
                new QueryExecutionContext()
                        .buildParams()
                        .integer("status", ContentStatus.VALIDATED.ordinal())
                        .integer("limit", limit)
                        .toContext()
        );
    }

    public void fetchLastViewedContent(List<ContentHeader> contentHeaders, int limit) {
        contentHeaderQueryTemplate.select(contentHeaders, "selectLimitedLastViewed",
                new QueryExecutionContext()
                        .buildParams()
                        .integer("status", ContentStatus.VALIDATED.ordinal())
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

        try {
            insertContentInElasticSearch(contentDetail.header().id());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return Long.valueOf(genKeys.get("id"));
    }

    public void reIndexAllInElasticSearch() {
        List<ContentDetail> contents = new ArrayList<ContentDetail>();
        contentDetailQueryTemplate.select(contents, "selectAll",
                new QueryExecutionContext().buildParams().toContext());

        for(ContentDetail contentDetail : contents) {
            try {
                insertContentInElasticSearch(contentDetail.header().id());
            } catch (IOException e) {
                e.printStackTrace();  //TODO: To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    // This method is not optimized.
    private void insertContentInElasticSearch(final Long contentId) throws IOException {
        esContentDao.asyncPrepareIndex(contentId.toString(),
                new ElasticSearchDao.XContentBuilderFactory() {
                    @Override
                    public XContentBuilder createXContentBuilder() throws IOException {
                        // Get the content from DB.
                        ContentDetail contentDetail = contentDetailQueryTemplate.selectObject("selectById",
                                new QueryExecutionContext().buildParams()
                                        .bigint("id", contentId)
                                        .toContext()
                        );

                        // Get all the id of the domains linked with the content.
                        List<Long> domainIds = new ArrayList<>();

                        for (Domain domain : contentDetail.header().domains()) {
                            domainIds.add(domain.id());
                        }

                        // Build the query to store content into ES.
                        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field(ES_CONTENT_FIELD_TITLE, contentDetail.header().title())
                                .field(ES_CONTENT_FIELD_DESCRIPTION, contentDetail.header().description())
                                .field(ES_CONTENT_FIELD_CONTENT_TYPE, contentDetail.header().type().ordinal())
                                .field(ES_CONTENT_FIELD_CREATION_DATE, contentDetail.header().creationDate())
                                .field(ES_CONTENT_FIELD_LAST_MODIF_DATE, contentDetail.header().lastModificationDate())
                                .field(ES_CONTENT_FIELD_DOMAINS, domainIds)
                                .field(ES_CONTENT_FIELD_TAGS, contentDetail.header().tags())
                                .field(ES_CONTENT_FIELD_STATUS, contentDetail.header().status().ordinal())
                                .field(ES_CONTENT_FIELD_ANCESTOR, contentDetail.header().ancestorId())
                                        //.field("version", contentDetail.header().version())
                                .field(ES_CONTENT_FIELD_AUTHOR, contentDetail.header().author().id());

                        // Indexing file content only if content.url is set
                        if (contentDetail.url() != null) {
                            Path contentPath = Paths.get(contentDetail.url());
                            // TODO : Optimize memory consumption here ???
                            // This will potentially take a large amount of memory since file to index
                            // should be entirely loaded into memory to be indexed by elastic search
                            // I already tested rawField(ES_CONTENT_FIELD_FILECONTENT, Files.newInputStream(contentPath))
                            // but it doesn't seem to work under elastic search 0.17.6
                            xContentBuilder.field(ES_CONTENT_FIELD_FILECONTENT, Files.readAllBytes(contentPath));
                        }

                        return xContentBuilder;
                    }
                });


//        uncomment this code to verify fileContent is correctly indexed
//                String searchTerms = "J2EE";
//                CountResponse res = client.count(countRequest(ES_INDEX_NAME).query(fieldQuery(ES_CONTENT_FIELD_FILECONTENT, searchTerms))).actionGet();
//                System.out.println(res.count());
    }

    public void updateContentOfContent(Long contentId, String url) {
        Date currentDate = new DateMidnight().toDate();
        contentHeaderQueryTemplate.update("updateContentUrl",
                new QueryExecutionContext().buildParams()
                        .string("url", url)
                        .bigint("id", contentId)
                        .date("lastModificationDate", currentDate)
                        .toContext());
        // TODO: check the number of row updated.

        try {
            insertContentInElasticSearch(contentId);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateContent(ContentDetail contentDetail) {
        Date currentDate = new DateMidnight().toDate();

        // If the content status was VALIDATED or REJECTED then new version of the content
        ContentDetail contentDetailFromDb = contentDetailQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", contentDetail.header().id())
                        .toContext()
        );

        Long contentIdToUse;
        // Content edition workflow: duplication on VALIDATED and REJECTED statuses
        if(ContentStatus.VALIDATED.equals(contentDetailFromDb.header().status()) ||
                ContentStatus.REJECTED.equals(contentDetailFromDb.header().status())) {

            contentDetailFromDb.header().ancestorId(contentDetailFromDb.header().id());
            contentIdToUse = createContent(contentDetailFromDb);
        } else {
            contentIdToUse = contentDetail.header().id();
        }

        // Update the content in the DB
        contentHeaderQueryTemplate.update("updateContent",
                new QueryExecutionContext().buildParams()
                        .string("title", contentDetail.header().title())
                        .string("description", contentDetail.header().description())
                        .string("tags", listTagsToString(contentDetail.header().tags()))
                        .bigint("id", contentIdToUse)
                        .date("lastModificationDate", currentDate)
                        .toContext());

        List<Long> newDomainIds = new ArrayList<Long>();

        // Check tags
        for (String tag : contentDetail.header().tags()) {
            tagDao.createOrUpdateTag(tag);
        }

        // Get current domain ids that are linked with the content.
        List<Long> domainsIds = getDomainIds(contentIdToUse);

        // Check added domains
        for (Domain domainToCheck : contentDetail.header().domains()) {
            newDomainIds.add(domainToCheck.id());

            if (!domainsIds.contains(domainToCheck.id())) {
                // Add new link with domains
                contentHeaderQueryTemplate.insert("addLinkWithDomain",
                        new QueryExecutionContext().buildParams()
                                .bigint("contentId", contentIdToUse)
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
                                .bigint("contentId", contentIdToUse)
                                .bigint("domainId", domainIdToCheckForRemove)
                                .toContext());
            }
        }

        // Insert into ES the content.
        try {
            insertContentInElasticSearch(contentIdToUse);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void fetchByIds(List<ContentHeader> contentHeaders, List<Long> contentIds) {
        // Fetch the contents from the database via the content ids.
        if (!contentIds.isEmpty()) {
            for (Long contentId : contentIds) {
                contentHeaders.add(contentHeaderQueryTemplate.selectObject("selectById",
                        new QueryExecutionContext().buildParams()
                                .bigint("id", contentId)
                                .toContext()));
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
        List<Long> contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), elasticSearchQuery);
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

        RangeFilterBuilder rangeFilter = configureCreationDateFilter(query.from(), query.to());

        List<Long> contentIds;
        if (rangeFilter == null) {
            contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), elasticSearchQuery);
        } else {
            QueryBuilder filteredQuery = QueryBuilders.filteredQuery(elasticSearchQuery, rangeFilter);
            contentIds = searchInElasticSearch(query.startingOffset(), query.numberOfContents(), filteredQuery);
        }
        fetchByIds(contentHeaders, contentIds);
   }

    private void configureAuthorsQuery(BoolQueryBuilder elasticSearchQuery, String[] authors) {
        if (authors != null && authors.length > 0) {
            elasticSearchQuery.must(QueryBuilders.termsQuery(ES_CONTENT_FIELD_AUTHOR, authors));
        }
    }

    private BoolQueryBuilder createElasticSearchQuery(List<Integer> statuses) {
        return boolQuery().must(inQuery(ES_CONTENT_FIELD_STATUS, statuses.toArray()));
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
                        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                        .setQuery(elasticSearchQuery)
                        .setFrom(startingOffset).setSize(numberOfContents)
                        .addSort("_score", SortOrder.DESC)
                        //.setMinScore(0.3f)
                        .execute()
                        .actionGet();

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
        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter("creationDate");
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
                    .add(termQuery(ES_CONTENT_FIELD_TITLE, textToSearch).boost(5))
                    .add(termQuery(ES_CONTENT_FIELD_DESCRIPTION, textToSearch).boost(3))
                    .add(termQuery(ES_CONTENT_FIELD_FILECONTENT, textToSearch).boost(4))
                    .add(termQuery(ES_CONTENT_FIELD_TAGS, textToSearch).boost(5)));
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
            elasticSearchQuery.must(QueryBuilders.termsQuery(ES_CONTENT_FIELD_DOMAINS, domains));
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
            elasticSearchQuery.must(QueryBuilders.termsQuery(ES_CONTENT_FIELD_CONTENT_TYPE, searchTypes));
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

    public void updateContentStatus(Long id, ContentStatus status, String comment) {
        Date currentDate = new DateMidnight().toDate();

        // TODO check the status and check if it is possible to go to this status (workflow).
        // Check user rights...

        contentHeaderQueryTemplate.update("updateStatus",
                new QueryExecutionContext()
                        .buildParams()
                        .bigint("id", id)
                        .integer("status", status.ordinal())
                        .date("lastModificationDate", currentDate)
                        .toContext()
        );

        try {
            insertContentInElasticSearch(id);
        } catch (IOException e) {
            e.getMessage();
        }

        if (comment != null && !comment.isEmpty()) {
            contentHeaderQueryTemplate.insert("addComment",
                    new QueryExecutionContext()
                            .buildParams()
                            .bigint("id", id)
                            .string("comment", comment)
                            .toContext(),
                    "id"
            );
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
            contentDetail.header(contentHeader);
            contentDetail.url(resultSet.getString("FILE_URI"));

            return contentDetail;
        }
    }
}
