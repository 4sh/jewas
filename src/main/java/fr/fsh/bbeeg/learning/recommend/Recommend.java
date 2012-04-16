package fr.fsh.bbeeg.learning.recommend;

import fr.fsh.bbeeg.learning.resources.MachineLearningResource;
import fr.fsh.bbeeg.learning.similarity.BBEEGTagSimilarity;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: florent
 * Date: 4/10/12
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recommend {

    public static final Logger logger = LoggerFactory.getLogger(Recommend.class);

    Recommender recommender;
    List<RecommendedItem> recommendedItems;
    DataSource source;
    MachineLearningResource mlRes;


    public Recommend(DataSource _source, MachineLearningResource _mlRes){
        source = _source;
        mlRes = _mlRes;
    }



    public void testRecommend(long userID, double coefDomain, double coefAuthor, double coefTags){

        //DataSource source = new MysqlConnectionPoolDataSource();
        //MachineLearningDao mlDao = new MachineLearningDao(source);
        //MachineLearningResource mlRes = new MachineLearningResource(mlDao);
        JDBCDataModel dataModel = new MySQLBooleanPrefJDBCDataModel(source,"PREFERENCES","USER_ID","ITEM_ID",null);
        BBEEGTagSimilarity sim = new BBEEGTagSimilarity(dataModel,coefDomain,coefAuthor,coefTags,mlRes);
        ItemBasedRecommender itemBasedRecommender = new GenericBooleanPrefItemBasedRecommender(dataModel, sim);
        try{
            this.recommend(itemBasedRecommender, userID, 5);
        } catch (TasteException te){
            logger.error("Recommendation error",te);
        }
        this.displayRecommendations();
    }
    
    public void recommend(Recommender _recommender,  long userID, int nbRecommendations) 
            throws TasteException{
        this.recommender = _recommender;
        this.recommendedItems = recommender.recommend(userID,nbRecommendations);

    }    
    
    private void displayRecommendations(){
        //TODO:User display return

        if (recommendedItems == null){

        }else{
            try{
                Iterator<RecommendedItem> it = recommendedItems.listIterator();
                while (it.hasNext()){
                    System.out.println(it.next().toString());
                }
            }catch(NullPointerException npe){
                logger.error("No recommendation found");
            }
        }
    }
        
    

}
