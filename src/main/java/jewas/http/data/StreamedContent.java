package jewas.http.data;

import org.apache.commons.lang.NotImplementedException;

/**
 * @author fcamblor
 * Implementation for rough body content, for example if we pass Json content
 * (not named) in reauest body
 */
public class StreamedContent implements HttpData {
    @Override
    public boolean isCompleted() {
        throw new NotImplementedException();
    }
}
