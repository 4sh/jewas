package jewas.http.data;

/**
 * @author fcamblor
 * Interface representation for http data read in body content
 * Every implementations should be immutable !
 */
public interface HttpData {
    public boolean isCompleted();
}
