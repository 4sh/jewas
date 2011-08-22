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
 * @author driccio
 */
public class DomainDao {
    private QueryTemplate<Domain> domainQueryTemplate;
    private I18nDao i18nDao;

    public DomainDao(DataSource dataSource, I18nDao _i18nDao) {
        i18nDao = _i18nDao;

        this.domainQueryTemplate =
                new QueryTemplate<Domain>(dataSource, new DomainToReadRowMapper())
                        .addQuery("selectById", "select * from Domain where id = :id")
                        .addQuery("selectByIds", "select * from Domain where id in (:id)")
                        .addQuery("selectAll", "select * from Domain")
                        .addQuery("selectLimitedPopular", // TODO: change request or remove it. Use elasticSearch insteed
                                "select * from (select * from Domain) " +
                                        "where ROWNUM <= :limit");
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
                            .array("ids", domainIds)
                            .toContext()
            );
        }

        return domains;
    }

    public List<Domain> getAllDomains() {
        List<Domain> domains = new ArrayList<Domain>();

        domainQueryTemplate.select(domains, "selectAll",
                new QueryExecutionContext().buildParams().toContext()
        );

        return domains;
    }

    public List<Domain> getPopularDomains(int limit) {
        List<Domain> domains = new ArrayList<Domain>();

        domainQueryTemplate.select(domains, "selectLimitedPopular",
                new QueryExecutionContext().buildParams()
                        .integer("limit", limit)
                        .toContext()
        );

        return domains;
    }

    private class DomainToReadRowMapper implements RowMapper<Domain> {
        @Override
        public Domain processRow(ResultSet rs) throws SQLException {
            return new Domain()
                    .id(rs.getLong("id"))
                    .label(i18nDao.translation(rs.getString("I18N_KEY")));
        }
    }

}
