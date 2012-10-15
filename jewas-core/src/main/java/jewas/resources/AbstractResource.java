package jewas.resources;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author fcamblor
 */
public abstract class AbstractResource implements Resource {
    private String path;

    protected AbstractResource(String path) {
        this.path = path;
    }

    public AbstractResource path(String _path) {
        this.path = _path;
        return this;
    }

    public String path() {
        return this.path;
    }

    public Path pathInCache(File cachedResourcesFileSystemRootDir) {
        return Paths.get(cachedResourcesFileSystemRootDir.getAbsolutePath(), path());
    }
}
