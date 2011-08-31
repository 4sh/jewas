package jewas.http.data;


/**
 * @author fcamblor
 * Implementation for rough body content, for example if we pass Json content
 * (not named) in reauest body
 */
public class StreamedContent implements HttpData {
    @Override
    public boolean isCompleted() {
        throw new RuntimeException("Not yet implemented");
    }
}
