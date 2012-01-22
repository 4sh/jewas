package fr.fsh.bbeeg.common.persistence;

import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.domain.pojos.Domain;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.RemoteTransportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.elasticsearch.client.Requests.createIndexRequest;
import static org.elasticsearch.client.Requests.putMappingRequest;

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