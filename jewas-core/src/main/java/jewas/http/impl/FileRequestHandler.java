package jewas.http.impl;

import jewas.http.FileResponse;
import jewas.http.HttpRequest;
import jewas.http.HttpStatus;
import jewas.http.RequestHandler;
import jewas.util.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

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
     * Class logger.
     */
    private Logger logger = LoggerFactory.getLogger(FileRequestHandler.class);

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
        File extractedFileInCache = new File(cachedResourcesFileSystemRootDir.getAbsolutePath() + File.separator + path);

        try {
            if (!extractedFileInCache.exists()) {
                Files.touchFileWithParents(extractedFileInCache);

                try (InputStream classloaderStream = Files.getInputStreamFromPath(path);
                     OutputStream fileSystemStream = new FileOutputStream(extractedFileInCache)) {
                    // Let's extract file from classpath...
                    Files.copyStreamTo(classloaderStream, fileSystemStream);
                }
            }
            FileResponse fileResponse = request.respondFile();
            if(headers != null){
                for(Map.Entry<String, String> headerEntry : headers.entrySet()){
                    fileResponse.addHeader(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            fileResponse.file(extractedFileInCache.toPath());
        } catch (IOException e) {
            logger.error("Error opening :" + extractedFileInCache.getAbsolutePath(), e);
            request.respondError(HttpStatus.NOT_FOUND);
        }
    }
}
