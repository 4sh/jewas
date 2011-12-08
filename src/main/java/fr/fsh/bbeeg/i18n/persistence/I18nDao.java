package fr.fsh.bbeeg.i18n.persistence;

import jewas.persistence.QueryExecutionContext;
import jewas.persistence.QueryTemplate;
import jewas.persistence.rowMapper.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author carmarolli
 */
public class I18nDao {
    private QueryTemplate<String> i18nQueryTemplate;

    public I18nDao(DataSource dataSource) {
        this.i18nQueryTemplate =
                new QueryTemplate<String>(dataSource, new I18nRowMapper())
                        .addQuery("selectByKeyAndLanguage", "select label from I18N_TABLE where I18N_KEY = :key and language = :language");
    }

    /**
     * Returns the database translation for the given internationalization key.
     *
     * @param key the internationalization key.
     * @return the translation corresponding to the given key and locale if found, the given key otherwise.
     */
    public String translation(String key, String locale) {
        String translation = i18nQueryTemplate.selectObject("selectByKeyAndLanguage",
                new QueryExecutionContext().buildParams()
                        .string("key", key)
                        .string("language", locale)
                        .toContext()
        );
        return translation == null ? key : translation;
    }

    private class I18nRowMapper implements RowMapper<String> {
        @Override
        public String processRow(ResultSet rs) throws SQLException {
            return rs.getString("label");
        }
    }
}
