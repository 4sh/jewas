package jewas.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author fcamblor
 */
public interface Resource {
    String path();
    InputStream in() throws IOException;
}
