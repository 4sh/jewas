package fr.fsh.bbeeg.content.persistence.mocks;

import fr.fsh.bbeeg.common.persistence.ElasticSearchDao;
import fr.fsh.bbeeg.content.pojos.ContentDetail;

public class ElasticSearchDaoMock extends ElasticSearchDao {


    public ElasticSearchDaoMock() {
        super(null, null, null, null);
    }


    public void createIndexIfNotExists() {

    }

    public void createIndexIfNotExists(String mapping) {

    }

    public static interface XContentBuilderFactory {

    }

    public void asyncPrepareIndex(final String id, final XContentBuilderFactory contentFactory) {

    }

    public String indexName() {
        return null;
    }

    public String indexType() {
        return null;
    }

    public void insertContentInElasticSearch(ContentDetail contentDetail) {
        
    }

}
