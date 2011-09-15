package fr.fsh.bbeeg.common.persistence;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.bootstrap.ElasticSearch;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.RemoteTransportException;

import java.io.IOException;
import java.util.concurrent.*;

import static org.elasticsearch.client.Requests.createIndexRequest;
import static org.elasticsearch.client.Requests.putMappingRequest;

/**
 * @author fcamblor
 */
public class ElasticSearches {

    private static final int MAXIMUM_NUMBER_OF_PARALLEL_INDEXING_THREADS = 4;
    private static final ElasticSearches INSTANCE = new ElasticSearches();
    private final ExecutorService INDEXING_EXECUTORS;

    private ElasticSearches(){
        INDEXING_EXECUTORS  = Executors.newFixedThreadPool(1, new ThreadFactory() {
            private ThreadFactory core = Executors.defaultThreadFactory();
            @Override
            public Thread newThread(Runnable runnable) {
                Thread t = core.newThread(runnable);
                t.setDaemon(true);
                return t;
            }
        });
    }

//    private static final ExecutorCompletionService INDEXING_EXECUTORS = new ExecutorCompletionService(
//            Executors.newFixedThreadPool(MAXIMUM_NUMBER_OF_PARALLEL_INDEXING_THREADS));


    public static void createIndexIfNotExists(Client esClient, String indexName){
        createIndexIfNotExists(esClient, indexName, null, null);
    }

    public static void createIndexIfNotExists(Client esClient, String indexName, String indexType, String mapping){
        try {
            esClient.admin().indices().create(createIndexRequest(indexName)).actionGet();
        }catch(RemoteTransportException e){
            // if an exception is thrown, it means index already exist
            // In this case, we should obfuscate the underlying exception
        }

        // Adding given mapping to the index
        // Note : we could have done this below when creating the index
        // Problem is : if index already exists, index mapping could not be updated incrementally.
        // This is the reason why we put mapping in a second time
        if(indexType != null && mapping != null){
            esClient.admin().indices().putMapping(putMappingRequest(indexName).type(indexType).source(mapping)).actionGet();
        }
    }

    // XContentBuilderFactory allowing to create XContentBuilder asynchroniously
    // For example when manipulating heavy data like file contents
    public static interface XContentBuilderFactory {
        public XContentBuilder createXContentBuilder() throws IOException;
    }

    public static ElasticSearches instance(){
        return INSTANCE;
    }

    public void asyncPrepareIndex(final Client esClient, final String indexName, final String indexType,
                                         final String id, final XContentBuilderFactory contentFactory){
        // Indexing content asynchronously
        /* I don't know why, executors are never called when async call is made ... :(
        INDEXING_EXECUTORS.submit(new Runnable() {
            @Override
            public void run() {
            */
                try {
                    XContentBuilder source = contentFactory.createXContentBuilder();
                    IndexResponse iResponse = esClient.prepareIndex(indexName, indexType, id)
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
}