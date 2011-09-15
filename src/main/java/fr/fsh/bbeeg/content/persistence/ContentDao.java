package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.common.persistence.ElasticSearches;
import fr.fsh.bbeeg.common.resources.Count;
import fr.fsh.bbeeg.content.pojos.*;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fr.fsh.bbeeg.content.pojos.SearchMode.values;

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

    public ContentDao(DataSource dataSource, Client _client, UserDao _userDao, DomainDao _domainDao) {
        client = _client;
        userDao = _userDao;
        domainDao = _domainDao;

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
                        .addQuery("insert", "INSERT INTO CONTENT (ID, TITLE, DESCRIPTION, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, CONTENT_TYPE, AUTHOR_REF, CONTENT_ANCESTOR_REF) " +
                                "VALUES (CONTENT_SEQ.nextval, :title, :description, :creationDate, :lastModificationDate, 0, :contentType, :authorId, :ancestorId)")
                        .addQuery("updateContentUrl", "UPDATE CONTENT " +
                                "SET FILE_URI = :url, STATUS = 0, LAST_MODIFICATION_DATE = :lastModificationDate " +
                                "WHERE ID = :id")
                        .addQuery("updateContent", "UPDATE CONTENT " +
                                "SET TITLE = :title, DESCRIPTION = :description, STATUS = 0, LAST_MODIFICATION_DATE = :lastModificationDate " +
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
                        .addQuery("selectById", "select * from Content where id = :id");

        this.idQueryTemplate =
                new QueryTemplate<Long>(dataSource, new LongRowMapper())
                        .addQuery("selectDomainIdsByContentId",
                                "select domain_ref as ID from Content_Domain " +
                                        "where content_ref = :id");

        // Initializing ES indexes
        String mappingSource = String.format("{ \"%s\" : { \"properties\" : { \"%s\" : { \"type\" : \"attachment\" } } } }",
                "content",
                "fileContent");
        ElasticSearches.createIndexIfNotExists(client, "bb-eeg", "content", mappingSource);
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
                                .integer("contentType", contentDetail.header().type().ordinal())
                                .bigint("authorId", 1000) // TODO: change 1000 with the current connected user id
                                .integer("status", ContentStatus.DRAFT.ordinal())
                                .date("creationDate", contentDetail.header().creationDate())
                                .date("lastModificationDate",
                                        contentDetail.header().lastModificationDate())
                                .bigint("ancestorId", contentDetail.header().ancestorId())
                                .toContext(),
                        "id");

        contentDetail.header().id(Long.parseLong(genKeys.get("id")));

        for (Domain domain : contentDetail.header().domains()) {
            contentHeaderQueryTemplate.insert("addLinkWithDomain",
                    new QueryExecutionContext().buildParams()
                            .bigint("contentId", Long.valueOf(genKeys.get("id")))
                            .bigint("domainId", domain.id())
                            .toContext());
        }

        try {
            insertContentInElasticSearch(contentDetail.header().id());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return Long.valueOf(genKeys.get("id"));
    }

    // This method is not optimized.
    private void insertContentInElasticSearch(final Long contentId) throws IOException {
        ElasticSearches.instance().asyncPrepareIndex(client, "bb-eeg", "content", contentId.toString(),
                new ElasticSearches.XContentBuilderFactory(){
                    @Override
                    public XContentBuilder createXContentBuilder() throws IOException {
                        // Get the content from DB.
                        ContentDetail contentDetail = contentDetailQueryTemplate.selectObject("selectById",
                                new QueryExecutionContext().buildParams()
                                        .bigint("id", contentId)
                                        .toContext()
                        );

                        // TODO use url to get the content and fetch it into ES

                        // Get all the id of the domains linked with the content.
                        List<Long> domainIds = new ArrayList<>();

                        for (Domain domain: contentDetail.header().domains()) {
                            domainIds.add(domain.id());
                        }

                        // Build the query to store content into ES.
                        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", contentDetail.header().title())
                                .field("description", contentDetail.header().description())
                                .field("contentType", contentDetail.header().type().ordinal())
                                .field("creationDate", contentDetail.header().creationDate())
                                .field("lastModificationDate", contentDetail.header().lastModificationDate())
                                .field("domains", domainIds)
                                .field("status", contentDetail.header().status().ordinal())
                                .field("fileContent", "")
                                .field("ancestor", contentDetail.header().ancestorId())
                                //.field("version", contentDetail.header().version())
                                .field("author", contentDetail.header().author().id());

                        // Indexing file content only if content.url is set
                        if(contentDetail.url() != null){
                            Path contentPath = Paths.get(contentDetail.url());
                            // TODO : Optimize memory consumption here ???
                            // This will potentially take a large amount of memory since file to index
                            // should be entirely loaded into memory to be indexed by elastic search
                            // I already tested rawField(ES_CONTENT_FIELD_FILECONTENT, Files.newInputStream(contentPath))
                            // but it doesn't seem to work under elastic search 0.17.6
                            xContentBuilder.field("fileContent", Files.readAllBytes(contentPath));
                        }

                        return xContentBuilder;
                    }
                });
        
        // uncomment this code to verify fileContent is correctly indexed
        //        String searchTerms = "J2EE";
        //        CountResponse res = client.count(countRequest(ES_INDEX_NAME).query(fieldQuery(ES_CONTENT_FIELD_FILECONTENT, searchTerms))).actionGet();
        //        System.out.println(res.count());
    }

    public void updateContentOfContent(Long contentId, ContentType contentType, String url) {
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
                        .bigint("id", contentIdToUse)
                        .date("lastModificationDate", currentDate)
                        .toContext());

        List<Long> newDomainIds = new ArrayList<Long>();

        // Get current domain ids that are linked with the content.
        List<Long> domainsIds = getDomainIds(contentIdToUse);

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

        for (Long domainIdToCheckForRemove : domainsIds) {
            boolean found = false;

            for (Domain domain : contentDetail.header().domains()) {
                if (domainIdToCheckForRemove == domain.id()) {
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

        if (query.searchMode() >= values().length) {
            // TODO: throw an exception
        }

        List<Integer> statuses = new ArrayList<Integer>();

        switch (values()[query.searchMode()]) {
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

        BoolQueryBuilder disMaxQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.inQuery("status", statuses.toArray()))
                      .must(QueryBuilders.termQuery("author", 1000)); // TODO: replace 1000 by the current user id.
                //.must(QueryBuilders.rangeQuery("lastModificationDate").lt(serverTimestamp)); // TODO: Use serverTImeStamp to filter

        if (!textToSearch.isEmpty()) {
            disMaxQueryBuilder.must(QueryBuilders.disMaxQuery()
                    .add(QueryBuilders.termQuery("title", textToSearch).boost(5))
                    .add(QueryBuilders.termQuery("description", textToSearch).boost(3))
                    .add(QueryBuilders.termQuery("fileContent", textToSearch).boost(4)));
        }

        // Search into elasticSearch.
        SearchResponse sResponse = client.prepareSearch("bb-eeg")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(disMaxQueryBuilder)
                .setFrom(query.startingOffset()).setSize(query.numberOfContents())
                .addSort("_score", SortOrder.DESC)
                .setMinScore(0.3f)
                .execute()
                .actionGet();



        // Get the content ids from the result.
        List<Long> contentIds = new ArrayList<Long>();

        for (SearchHit searchHit : sResponse.getHits()) {
            contentIds.add(Long.parseLong(searchHit.id()));
        }

        fetchByIds(contentHeaders, contentIds);
    }

    public void fetchSearch(List<ContentHeader> results, AdvancedSearchQueryObject query) {
        // TODO: implement it
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
