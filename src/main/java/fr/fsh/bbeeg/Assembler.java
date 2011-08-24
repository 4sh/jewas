package fr.fsh.bbeeg;


import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.resources.ContentResource;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.resources.DomainResource;
import fr.fsh.bbeeg.i18n.persistence.I18nDao;
import fr.fsh.bbeeg.user.persistence.UserDao;
import fr.fsh.bbeeg.user.resources.UserResource;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;


/**
 * @author driccio
 */
public class Assembler {
    private DataSource dataSource;

    /* Daos */
    private ContentDao contentDao;
    private UserDao userDao;
    private DomainDao domainDao;
    private I18nDao i18nDao;

    /* Resources */
    private ContentResource contentResource;
    private DomainResource domainResource;
    private UserResource userResource;

    public Assembler() {
        dataSource = createDatasource();

        i18nDao = new I18nDao(dataSource);
        domainDao = new DomainDao(dataSource, i18nDao);
        userDao = new UserDao(dataSource, domainDao);
        contentDao = new ContentDao(dataSource, userDao, domainDao);

        contentResource = new ContentResource(contentDao,
                BBEEGConfiguration.INSTANCE.cliOptions().contentFileRepository());
        domainResource = new DomainResource(domainDao);
        userResource = new UserResource(userDao);
    }

    private DataSource createDatasource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:h2:mem:mytest");
        ds.setDriverClassName("org.h2.Driver");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    /* ***** Resources ***** */
    public ContentResource contentResource() {
        return contentResource;
    }

    public DomainResource domainResource() {
        return domainResource;
    }

    public UserResource userResource() {
        return userResource;
    }
}
