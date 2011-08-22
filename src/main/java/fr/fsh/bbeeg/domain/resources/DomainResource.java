package fr.fsh.bbeeg.domain.resources;

import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;

import java.util.List;

/**
 * @author driccio
 */
public class DomainResource {
    private DomainDao domainDao;

    public DomainResource(DomainDao domainDao) {
        this.domainDao = domainDao;
    }

    public List<Domain> getPopularDomain(LimitedOrderedQueryObject loqo) {
//        List<DomainSearchResult> list = new ArrayList<DomainSearchResult>();
//
//        for (int i = 0; i < loqo.number(); i++) {
//            list.add(new DomainSearchResult("Domain" + i, new BigDecimal(new Random().nextInt(10)), ""));
//        }

        return domainDao.getPopularDomains(loqo.number());
    }
}
