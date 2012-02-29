package fr.fsh.bbeeg.common.persistence;

import fr.fsh.bbeeg.content.pojos.*;
import fr.fsh.bbeeg.domain.pojos.Domain;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.RemoteTransportException;
import org.joda.time.DateMidnight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static fr.fsh.bbeeg.content.pojos.SearchMode.values;
import static org.elasticsearch.client.Requests.createIndexRequest;
import static org.elasticsearch.client.Requests.putMappingRequest;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author fcamblor
 */
public class ElasticSearchDao {

    public static final String ES_CONTENT_FIELD_FILECONTENT = "fileContent";
    public static final String ES_CONTENT_FIELD_AUTHOR = "author";
    public static final String ES_CONTENT_FIELD_TITLE = "title";
    public static final String ES_CONTENT_FIELD_DESCRIPTION = "description";
    public static final String ES_CONTENT_FIELD_CONTENT_TYPE = "contentType";
    public static final String ES_CONTENT_FIELD_CREATION_DATE = "creationDate";
    public static final String ES_CONTENT_FIELD_LAST_MODIF_DATE = "lastModificationDate";
    public static final String ES_CONTENT_FIELD_PUBLICATION_START_DATE = "startPublicationDate";
    public static final String ES_CONTENT_FIELD_PUBLICATION_END_DATE = "endPublicationDate";
    public static final String ES_CONTENT_FIELD_DOMAINS = "domains";
    public static final String ES_CONTENT_FIELD_TAGS = "tags";
    public static final String ES_CONTENT_FIELD_STATUS = "status";
    public static final String ES_CONTENT_FIELD_ANCESTOR = "ancestor";

    private final Client elasticSearchClient;
    private final String indexName;
    private final String indexType;

    private final ExecutorService indexingExecutors;

    public ElasticSearchDao(Client esClient, String indexName, String indexType,
                            ExecutorService indexingExecutors){
        this.elasticSearchClient = esClient;
        this.indexName = indexName;
        this.indexType = indexType;
        this.indexingExecutors = indexingExecutors;
    }

    public List<Long> search(SimpleSearchQueryObject query) {
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
        return contentIds;
    }

    public List<Long> search(AdvancedSearchQueryObject query) {
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
        return contentIds;
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
        SearchResponse sResponse = elasticSearchClient.prepareSearch(indexName())
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

    // This method is not optimized.
    public void insertContentInElasticSearch(final ContentDetail contentDetail) throws IOException {
            asyncPrepareIndex(contentDetail.header().id().toString(),
                new ElasticSearchDao.XContentBuilderFactory() {
                    @Override
                    public XContentBuilder createXContentBuilder() throws IOException {

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
                                .field(ES_CONTENT_FIELD_PUBLICATION_START_DATE, contentDetail.header().startPublicationDate())
                                .field(ES_CONTENT_FIELD_PUBLICATION_END_DATE, contentDetail.header().endPublicationDate())
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

    public void createIndexIfNotExists(){
        createIndexIfNotExists(null);
    }

    public void createIndexIfNotExists(String mapping){
        try {
            elasticSearchClient.admin().indices().create(createIndexRequest(indexName)).actionGet();
        }catch(RemoteTransportException e){
            // if an exception is thrown, it means index already exist
            // In this case, we should obfuscate the underlying exception
        }

        // Adding given mapping to the index
        // Note : we could have done this below when creating the index
        // Problem is : if index already exists, index mapping could not be updated incrementally.
        // This is the reason why we put mapping in a second time
        if(indexType != null && mapping != null){
            elasticSearchClient.admin().indices().putMapping(putMappingRequest(indexName).type(indexType).source(mapping)).actionGet();
        }
    }

    // XContentBuilderFactory allowing to create XContentBuilder asynchroniously
    // For example when manipulating heavy data like file contents
    public static interface XContentBuilderFactory {
        public XContentBuilder createXContentBuilder() throws IOException;
    }

    public void asyncPrepareIndex(final String id, final XContentBuilderFactory contentFactory){
        // Indexing content asynchronously
        indexingExecutors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(String.format("indexing %s.%s with id %s ...", indexName, indexType, id));
                    XContentBuilder source = contentFactory.createXContentBuilder();
                    IndexResponse iResponse = elasticSearchClient.prepareIndex(indexName, indexType, id)
                            .setSource(source).execute().actionGet();
                } catch (IOException e) {
                    // FIXME : queue another indexation here ?
                    throw new RuntimeException("Asynchronous index preparation failed : " + e.getMessage(), e);
                }
            }
        });
    }

    public String indexName(){
        return this.indexName;
    }

    public String indexType(){
        return this.indexType;
    }
}