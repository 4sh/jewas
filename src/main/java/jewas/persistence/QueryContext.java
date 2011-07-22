package jewas.persistence;

import java.util.Map;

/**
 * Class will contain every Query informations like pagination parameters,
 * query parameters, ordering etc..
 *
 * @author fcamblor
 */
public class QueryContext {

    private Map<String, Object> queryParameters;

    public Map<String, Object> queryParameters() {
        return queryParameters;
    }

    public QueryContext queryParameters(Map<String, Object> p) {
        this.queryParameters = p;
        return this;
    }
}
