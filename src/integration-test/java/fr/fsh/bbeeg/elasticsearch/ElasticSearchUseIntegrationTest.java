package fr.fsh.bbeeg.elasticsearch;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/19/11
 * Time: 6:03 PM
 */
public class ElasticSearchUseIntegrationTest {

    private static Client client = null;

    @BeforeClass
    public static void start() {
        // It is deliberate we don't use the nodeBuild (with local() mode) here (used generally in unit tests)
        // We want to remotely contact an elastic search server started during the integration test phase

        client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        //client = NodeBuilder.nodeBuilder().node().client();
    }

    @AfterClass
    public static void stop() {
        client.close();
        client = null;
    }

    @Test
    public void creationAndRetrievementUseCase() throws IOException {
        IndexResponse iResponse = client.prepareIndex("twitter", "tweet", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elastic Search")
                        .endObject()
                )
                .execute()
                .actionGet();

        assertThat(iResponse, is(notNullValue()));
        assertThat(iResponse.id(), is(equalTo("1")));

        SearchResponse sResponse = client.prepareSearch("twitter")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setScroll("10m")
                .setQuery(QueryBuilders.termQuery("user", "kimchy"))
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();

        assertThat(sResponse, is(notNullValue()));
        assertThat(sResponse.getHits().hits()[0].getSource().get("message"), is(notNullValue()));
        assertThat(sResponse.getHits().hits()[0].getSource().get("message").toString(), is(equalTo("trying out Elastic Search")));


        GetResponse gResponse = client.prepareGet("twitter", "tweet", "1").execute().actionGet();
        assertThat(gResponse, is(notNullValue()));
        assertThat(gResponse.getSource().get("message").toString(), is(equalTo("trying out Elastic Search")));
    }

}
