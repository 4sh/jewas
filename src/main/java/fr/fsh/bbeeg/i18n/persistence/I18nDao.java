package fr.fsh.bbeeg.i18n.persistence;

import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author driccio
 */
public class I18nDao {
    private QueryTemplate<String> i18nQueryTemplate;

    public I18nDao(DataSource dataSource) {
        this.i18nQueryTemplate =
                new QueryTemplate<String>(dataSource, new I18nRowMapper())
                        .addQuery("selectByKeyAndLanguage", "select label from I18N_TABLE where I18N_KEY = :key and language = :language");
    }

    public String translation(String key) {
        return i18nQueryTemplate.selectObject("selectByKeyAndLanguage",
                new QueryExecutionContext().buildParams()
                        .string("key", key)
                        .string("language", "fr")
                        .toContext()
        );
    }

    private class I18nRowMapper implements RowMapper<String> {
        @Override
        public String processRow(ResultSet rs) throws SQLException {
            return rs.getString("label");
        }
    }
}
