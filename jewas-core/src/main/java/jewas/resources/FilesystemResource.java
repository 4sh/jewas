package jewas.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author fcamblor
 */
public class FilesystemResource extends AbstractResource {

    private File rootDirectory;

    public FilesystemResource(File rootDirectory, String path) {
        super(path);
        this.rootDirectory = rootDirectory;
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return Files.newInputStream(absolutePath());
    }

    public Path absolutePath() {
        return rootDirectory.toPath().resolve(path());
    }

    @Override
    public Path pathInCache(File cachedResourcesFileSystemRootDir) {
        // We shouldn't use the cache for file system resources
        return absolutePath();
    }
}
