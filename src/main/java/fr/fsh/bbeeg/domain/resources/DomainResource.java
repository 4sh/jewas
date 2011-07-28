package fr.fsh.bbeeg.domain.resources;

import fr.fsh.bbeeg.common.resources.LimitedOrderedQueryObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author driccio
 */
public class DomainResource {

    public static List<DomainSearchResult> getPopularDomain(LimitedOrderedQueryObject loqo) {
        List<DomainSearchResult> list = new ArrayList<DomainSearchResult>();

        for (int i = 0; i < loqo.number(); i++) {
            list.add(new DomainSearchResult("Domain" + i, new BigDecimal(new Random().nextInt(10)), ""));
        }

        return list;
    }
}
