package fr.fsh.bbeeg.content.persistence.mocks;

import fr.fsh.bbeeg.domain.persistence.DomainDao;
import fr.fsh.bbeeg.domain.pojos.Domain;

import java.util.List;

public class DomainDaoMock extends DomainDao {

    public DomainDaoMock() {
        super(null, null);
    }

    public Domain getDomain(Long domainId) {
        return null;
    }

    public List<Domain> getDomains(List<Long> domainIds) {
        return null;
    }

    public void fetchAllDomains(List<Domain> domains) {

    }

    public void fetchPopularDomains(List<Domain> domains, int limit) {

    }

}
