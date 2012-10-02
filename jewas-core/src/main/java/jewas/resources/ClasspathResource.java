package jewas.resources;

import jewas.util.file.Files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author fcamblor
 */
public class ClasspathResource extends AbstractResource {

    public ClasspathResource(String path) {
        super(path);
    }

    @Override
    public InputStream in() throws IOException {
        return Files.getInputStreamFromPath(this.path());
    }
}
