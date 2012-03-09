package fr.fsh.bbeeg.domain.persistence;

import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.i18n.persistence.I18nDao;
import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author carmarolli
 */
public class DomainDao {
    private QueryTemplate<Domain> domainQueryTemplate;
    private I18nDao i18nDao;

    public DomainDao(DataSource dataSource, I18nDao _i18nDao) {
        i18nDao = _i18nDao;

        this.domainQueryTemplate =
                new QueryTemplate<Domain>(dataSource, new DomainToReadRowMapper())
                        .addQuery("selectById",
                                "select * from DOMAIN where ID = :id")
                        .addQuery("selectByIds",
                                "select * from DOMAIN where ID in :ids")
                        .addQuery("selectAll",
                                "select * from DOMAIN where ID not in (select distinct PARENT_REF from DOMAIN where PARENT_REF is not null)")
                        .addQuery("selectAllHierarchy",
                                "select * from DOMAIN order by LEVEL asc")
                        .addQuery("selectLimitedPopular",
                                "select * from DOMAIN limit :limit");
    }

    public Domain getDomain(Long domainId) {
        Domain domain = domainQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", domainId)
                        .toContext()
        );

        return domain;
    }

    public List<Domain> getDomains(List<Long> domainIds) {
        List<Domain> domains = new ArrayList<Domain>();

        if (domainIds != null && !domainIds.isEmpty()) {
            domainQueryTemplate.select(domains, "selectByIds",
                    new QueryExecutionContext().buildParams()
                            .array("ids", domainIds.toArray())
                            .toContext()
            );
        }

        return domains;
    }

    public void fetchAllDomains(List<Domain> domains) {
        domainQueryTemplate.select(domains, "selectAll",
                new QueryExecutionContext().buildParams().toContext()
        );
    }

    /**
     * Fetch all the domains ordered by their level in the domain hierarchy.
     *
     * @param domains the collection of loaded domains.
     */
    public void fetchAllDomainHierarchy(List<Domain> domains) {
        domainQueryTemplate.select(domains, "selectAllHierarchy",
                new QueryExecutionContext().buildParams().toContext()
        );
    }

    public void fetchPopularDomains(List<Domain> domains, int limit) {
        domainQueryTemplate.select(domains, "selectLimitedPopular",
                new QueryExecutionContext().buildParams()
                        .integer("limit", limit)
                        .toContext()
        );
    }

    private class DomainToReadRowMapper implements RowMapper<Domain> {
        @Override
        public Domain processRow(ResultSet rs) throws SQLException {
            return new Domain()
                    .id(rs.getLong("ID"))
                    .label(i18nDao.translation(rs.getString("I18N_KEY"), "fr"))
                    .parentRef(rs.getLong("PARENT_REF"))
                    .level(rs.getInt("LEVEL"));
        }
    }
}
