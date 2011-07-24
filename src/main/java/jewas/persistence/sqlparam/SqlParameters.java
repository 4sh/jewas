package jewas.persistence.sqlparam;

/**
 * @author fcamblor
 */
public class SqlParameters {
    public static SqlParameter.Builder madeOf() {
        return new SqlParameter.Builder();
    } // Syntaxic sugar
}
