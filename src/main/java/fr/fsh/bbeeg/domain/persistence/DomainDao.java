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
    private QueryTemplate<Domain> domainToReadQueryTemplate;
    private I18nDao i18nDao;

    public DomainDao(DataSource dataSource, I18nDao _i18nDao) {
        i18nDao = _i18nDao;

        this.domainToReadQueryTemplate =
                new QueryTemplate<Domain>(dataSource, new DomainToReadRowMapper())
                        .addQuery("selectById", "select * from Domain where id = :id")
                        .addQuery("selectByIds", "select * from Domain where id in (:id)");
    }

    public Domain getDomainToRead(Long domainId) {
        Domain domain = domainToReadQueryTemplate.selectObject("selectById",
                new QueryExecutionContext().buildParams()
                        .bigint("id", domainId)
                        .toContext()
        );

        return domain;
    }

    public List<Domain> getDomainsToRead(List<Long> domainIds) {
        List<Domain> domains = new ArrayList<Domain>();

        if (domainIds != null && !domainIds.isEmpty()) {
            domainToReadQueryTemplate.select(domains, "selectByIds",
                    new QueryExecutionContext().buildParams()
                            .array("ids", domainIds)
                            .toContext()
            );
        }

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
