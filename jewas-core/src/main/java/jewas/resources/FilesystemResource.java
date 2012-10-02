package jewas.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public InputStream in() throws IOException {
        return Files.newInputStream(Paths.get(rootDirectory.getAbsolutePath(), path()));
    }
}
