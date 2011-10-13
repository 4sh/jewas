package fr.fsh.bbeeg;

import fr.fsh.bbeeg.common.CliOptions;
import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.persistence.ElasticSearchDao;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.resources.ContentResource;
import fr.fsh.bbeeg.content.resources.EegResource;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.resources.DomainResource;
import fr.fsh.bbeeg.i18n.persistence.I18nDao;
import fr.fsh.bbeeg.security.persistence.SecurityDao;
import fr.fsh.bbeeg.security.resources.ConnectedUserResource;
import fr.fsh.bbeeg.security.resources.SecurityResource;
import fr.fsh.bbeeg.tag.persistence.TagDao;
import fr.fsh.bbeeg.tag.resources.TagResource;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.resources.UserResource;
import org.apache.commons.dbcp.BasicDataSource;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.sql.DataSource;
import java.util.concurrent.Executors;


/**
 * @author driccio
 */
public class Assembler {
    private DataSource dataSource;
    private Client client = null;

    /* Elastic search DAOs */
    private ElasticSearchDao esContentDao;

    /* Daos */
    private ContentDao contentDao;
    private UserDao userDao;
    private DomainDao domainDao;
    private TagDao tagDao;
    private I18nDao i18nDao;
    private SecurityDao securityDao;

    /* Resources */
    private ContentResource contentResource;
    private DomainResource domainResource;
    private TagResource tagResource;
    private UserResource userResource;
    private ConnectedUserResource connectedUserResource;
    private EegResource eegResource;
    private SecurityResource securityResource;

    public Assembler(CliOptions options) {
        dataSource = createDatasource(options);
        client = new TransportClient().addTransportAddress(
                new InetSocketTransportAddress(
                        BBEEGConfiguration.INSTANCE.cliOptions().elasticSearchAdress(),
                        BBEEGConfiguration.INSTANCE.cliOptions().elasticSearchPort()));

        esContentDao = new ElasticSearchDao(client, "bb-eeg", "content",
                Executors.newFixedThreadPool(options.numberOfESContentIndexingThreads()));

        i18nDao = new I18nDao(dataSource);
        domainDao = new DomainDao(dataSource, i18nDao);
        tagDao = new TagDao(dataSource);
        userDao = new UserDao(dataSource, domainDao);
        contentDao = new ContentDao(dataSource, client, userDao, domainDao, esContentDao, tagDao);
        securityDao = new SecurityDao(dataSource);

        contentResource = new ContentResource(contentDao,
                BBEEGConfiguration.INSTANCE.cliOptions().contentFileRepository());
        domainResource = new DomainResource(domainDao);
        tagResource = new TagResource(tagDao);
        userResource = new UserResource(userDao);
        connectedUserResource = ConnectedUserResource.instance().userDao(userDao, securityDao);
        eegResource = new EegResource(contentDao,
                BBEEGConfiguration.INSTANCE.cliOptions().contentFileRepository());
        securityResource = new SecurityResource(securityDao);

    }

    private DataSource createDatasource(CliOptions options) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:h2:file:"+options.h2DbPath());
        ds.setDriverClassName("org.h2.Driver");
        ds.setUsername("sa");
        ds.setPassword("sa");
        return ds;
    }

    /* ***** Resources ***** */
    public ContentResource contentResource() {
        return contentResource;
    }

    public DomainResource domainResource() {
        return domainResource;
    }

    public TagResource tagResource() {
        return tagResource;
    }

    public UserResource userResource() {
        return userResource;
    }

    public EegResource eegResource() {
        return eegResource;
    }

    public SecurityResource securityResource() {
        return securityResource;
    }

    public ConnectedUserResource connectedUserResource() {
        return connectedUserResource;
    }
}
