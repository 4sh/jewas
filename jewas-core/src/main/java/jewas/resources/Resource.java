package jewas.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author fcamblor
 */
public interface Resource {
    String path();

    InputStream newInputStream() throws IOException;

    Path pathInCache(File cachedResourcesFileSystemRootDir);
}
