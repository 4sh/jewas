package jewas.http.impl;

import jewas.http.HttpRequest;
import jewas.http.HttpStatus;
import jewas.http.RequestHandler;
import jewas.util.file.Closeables;
import jewas.util.file.Files;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 16:50
 *
 * FileRequestHandler is a {@link RequestHandler} to use to load static resources.
 *
 */
public class FileRequestHandler extends AbstractRequestHandler {
    /**
     * The path of the file to load.
     */
    String path;

    /**
     *  Directory that will be used to extract files from jar to the filesystem the first
     *  time it is accessed
     */
    File cachedResourcesFileSystemRootDir;

    public FileRequestHandler(File cachedResourcesFileSystemRootDir, String path) {
        this.path = path;
        this.cachedResourcesFileSystemRootDir = cachedResourcesFileSystemRootDir;
    }

    /**
     * Loads the static file from the path into the given request.
     * @param request the {@link HttpRequest}
     */
    @Override
    public void onRequest(HttpRequest request) {
        File extractedFileInCache = new File(cachedResourcesFileSystemRootDir.getAbsolutePath()+File.separator+path);
        InputStream classloaderStream = null;
        OutputStream filesystemStream = null;
        try {
            if(!extractedFileInCache.exists()){
                Files.touchFileWithParents(extractedFileInCache);
                filesystemStream = new FileOutputStream(extractedFileInCache);
                classloaderStream = Files.getInputStreamFromPath(path);

                // Let's extract file from classpath...
                Files.copyStreamTo(classloaderStream, filesystemStream);
            }

            request.respondFile().file(extractedFileInCache.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            request.respondError(HttpStatus.NOT_FOUND);
        } finally {
            Closeables.defensiveClose(classloaderStream);
            Closeables.defensiveClose(filesystemStream);
        }
    }
}
