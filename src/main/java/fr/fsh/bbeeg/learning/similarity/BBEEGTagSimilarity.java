package fr.fsh.bbeeg.learning.similarity;

import fr.fsh.bbeeg.learning.resources.MachineLearningResource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: florent
 * Date: 4/5/12
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public final class BBEEGTagSimilarity implements ItemSimilarity{

    public static final Logger logger = LoggerFactory.getLogger(BBEEGTagSimilarity.class);

    private final DataModel dataModel;
    private final double cdomain;
    private final double cauthor;
    private final double ctags;
    private MachineLearningResource mlResource;


    public BBEEGTagSimilarity(DataModel dataModel,
                              double _cdomain, double _cauthor, double _ctags, MachineLearningResource _mlResource){
        super();
        if (dataModel == null){
            throw new IllegalArgumentException("null dataModel");
        }

        if (_cdomain+_cauthor+_ctags!=1.0){
            throw new IllegalArgumentException("coefficients sum not equal to 1");
        }
        this.dataModel = dataModel;
        this.cdomain = _cdomain;
        this.cauthor = _cauthor;
        this.ctags = _ctags;
        this.mlResource = _mlResource;
    }



    @Override
    public double itemSimilarity(long itemID1, long itemID2)
            throws TasteException{
        double testDomain = testDomain(itemID1, itemID2);
        double testAuthor = testAuthor(itemID1, itemID2);
        double  testTags  = testTags(itemID1, itemID2);
        return  testDomain*cdomain+testAuthor*cauthor+testTags*ctags;
    }

    private double computeCoefficient(List<String> list1, List<String> list2){
        BigDecimal total = new BigDecimal(list1.size()).setScale(4);
        BigDecimal common = BigDecimal.ZERO.setScale(4);

        Iterator<String> listIterator = list2.iterator();
        while (listIterator.hasNext()){
            if (list1.contains(listIterator.next())){
                common.add(BigDecimal.ONE);
            }
            else total.add(BigDecimal.ONE);
        }
        BigDecimal result = common.divide(total);

        return result.doubleValue();
    }
    
    
    private double testDomain(long itemID1,long itemID2){

        List<String> domain1 = Arrays.asList(mlResource.fetchContentDomain(itemID1));
        List<String> domain2 = Arrays.asList(mlResource.fetchContentDomain(itemID2));
        return computeCoefficient(domain1,domain2);

    }

    private double testAuthor(long itemID1, long itemID2){
        if (mlResource.fetchContentAuthor(itemID1)==mlResource.fetchContentAuthor(itemID2)){
            return 1.0;
        }
        return 0;
    }
    
    private double testTags(long itemID1, long itemID2){
        List<String> tags1 = Arrays.asList(mlResource.fetchContentTags(itemID1));
        List<String> tags2 = Arrays.asList(mlResource.fetchContentTags(itemID2));
        return computeCoefficient(tags1,tags2);
    }

    @Override
    public void refresh(Collection alreadyRefreshed){

    }

    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2)
            throws TasteException{
        double[] similarities = new double[itemID2.length];
        for (int i = 0;i<itemID2.length;i++){
            similarities[i] = this.itemSimilarity(itemID1,itemID2[i]);
        }
        return similarities;
    }

    @Override
    public long[] allSimilarItemIDs(long itemID)
            throws TasteException{
        FastIDSet allSimilarItemIDs = new FastIDSet();
        LongPrimitiveIterator allItemIDs = dataModel.getItemIDs();
        while(allItemIDs.hasNext()){
            long possiblySimilarItemID = allItemIDs.nextLong();
            if (!Double.isNaN(itemSimilarity(itemID,possiblySimilarItemID))){  //TODO:Set min similarity value
                allSimilarItemIDs.add(possiblySimilarItemID);

            }
        }
        return allSimilarItemIDs.toArray();
    }

}
