package fr.fsh.bbeeg.elasticsearch;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: fcamblor
 * Date: 7/19/11
 * Time: 6:03 PM
 */
public class ElasticSearchUseIntegrationTest {

    private Client client = null;

    @Before
    public void start(){
        // It is deliberate we don't use the nodeBuild (with local() mode) here (used generally in unit tests)
        // We want to remotely contact an elastic search server started during the integration test phase
        client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }

    @After
    public void stop(){
        client.close();
        client = null;
    }

    @Test
    public void creationAndRetrievementUseCase() throws IOException {
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
            .setSource(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("user", "kimchy")
                    .field("postDate", new Date())
                    .field("message", "trying out Elastic Search")
                    .endObject()
            )
            .execute()
            .actionGet();

        assertThat(response, is(notNullValue()));
        assertThat(response.id(), is(equalTo("1")));
    }
}
