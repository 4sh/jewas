package jewas.resources;

import jewas.util.file.Files;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    public URL url() throws IOException {
        return Files.getResourceFromPath(AbstractResource.class, path());
    }
}
