package fr.fsh.bbeeg.common.persistence;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.RemoteTransportException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static org.elasticsearch.client.Requests.createIndexRequest;
import static org.elasticsearch.client.Requests.putMappingRequest;

/**
 * @author fcamblor
 */
public class ElasticSearchDao {

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
        /*
        indexingExecutors.submit(new Runnable() {
            @Override
            public void run() {
            */
                try {
                    System.out.println(String.format("indexing %s.%s with id %s ...", indexName, indexType, id));
                    XContentBuilder source = contentFactory.createXContentBuilder();
                    IndexResponse iResponse = elasticSearchClient.prepareIndex(indexName, indexType, id)
                            .setSource(source).execute().actionGet();
                } catch (IOException e) {
                    // FIXME : queue another indexation here ?
                    throw new RuntimeException("Asynchronous index preparation failed : " + e.getMessage(), e);
                }
        /*
            }
        });
        */
    }

    public String indexName(){
        return this.indexName;
    }

    public String indexType(){
        return this.indexType;
    }
}