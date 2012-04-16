package fr.fsh.bbeeg.learning.resources;

import fr.fsh.bbeeg.learning.persistence.MachineLearningDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: florent
 * Date: 4/5/12
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachineLearningResource {

    public static final Logger logger = LoggerFactory.getLogger(MachineLearningResource.class);

    private MachineLearningDao mlDao;

    public MachineLearningResource(MachineLearningDao _mlDao){
        this.mlDao = _mlDao;
    }
    /*
    public MachineLearningDao getMlDao()
    {
        return this.mlDao;
    }
    */
    public String[] fetchContentTags(long itemID){
        String tags[] = mlDao.fetchContentTags(itemID);
        return tags;
    }
    
    public String[] fetchContentDomain(long itemID){
        String domain[] = mlDao.fetchContentDomain(itemID);
        return domain;
    }
    
    public Long fetchContentAuthor(long itemID){
        Long authorID = mlDao.fetchContentAuthor(itemID);
        return authorID;
    }

}
