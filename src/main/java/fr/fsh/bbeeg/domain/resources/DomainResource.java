package fr.fsh.bbeeg.domain.resources;

import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;
import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author driccio
 */
public class DomainResource {
    private DomainDao domainDao;

    public DomainResource(DomainDao domainDao) {
        this.domainDao = domainDao;
    }

    public void getPopularDomain(List<DomainSearchResult> results, LimitedOrderedQueryObject loqo) {
        List<Domain> domains = new ArrayList<Domain>();
        domainDao.fetchPopularDomains(domains, loqo.number());

        // TODO: use a right weight and set a url
        for (Domain domain: domains) {
            results.add(new DomainSearchResult(domain.label(),
                    new BigDecimal(new Random().nextInt(10)), ""));
        }
    }

    public void fetchAllDomains(List<Domain> results) {
        domainDao.fetchAllDomains(results);
    }
}
