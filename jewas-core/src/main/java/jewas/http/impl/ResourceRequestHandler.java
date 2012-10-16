package jewas.http.impl;

import jewas.configuration.JewasConfiguration;
import jewas.http.FileResponse;
import jewas.http.HttpRequest;
import jewas.http.HttpStatus;
import jewas.http.RequestHandler;
import jewas.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 16:50
 * <p/>
 * ResourceRequestHandler is a {@link RequestHandler} to use to load static resources.
 */
public class ResourceRequestHandler extends AbstractRequestHandler {

    /**
     * Class logger.
     */
    private Logger logger = LoggerFactory.getLogger(ResourceRequestHandler.class);

    /**
     * Will retain information of if a cached resource has already been checked for compliance
     * with the non cached one
     */
    private static final ConcurrentMap<String, Boolean> SAME_CACHED_FILE_CHECK_ON_PATH = new ConcurrentHashMap<String, Boolean>();

    /**
     * The resource to load.
     */
    Resource resource;

    /**
     * Directory that will be used to extract files from jar to the filesystem the first
     * time it is accessed
     */
    File cachedResourcesFileSystemRootDir;

    public ResourceRequestHandler(File cachedResourcesFileSystemRootDir, Resource resource) {
        this.resource = resource;
        this.cachedResourcesFileSystemRootDir = cachedResourcesFileSystemRootDir;
    }

    protected static boolean checkForCachedFileValidityNeededOn(Resource resource) {
        if (JewasConfiguration.devMode()) {
            return true;
        }
        if (SAME_CACHED_FILE_CHECK_ON_PATH.putIfAbsent(resource.path(), Boolean.FALSE) == null) {
            return true;
        }
        return false;
    }

    /**
     * Loads the static file from the path into the given request.
     *
     * @param request the {@link HttpRequest}
     */
    @Override
    public void onRequest(HttpRequest request) {
        Path extractedFileInCache = resource.pathInCache(cachedResourcesFileSystemRootDir);

        try {
            // Creating file in filesystem cache if it doesn't yet exist
            if (Files.notExists(extractedFileInCache)) {
                Path parentDirectoryInCache = extractedFileInCache.getParent();
                if (Files.notExists(parentDirectoryInCache)) {
                    Files.createDirectories(parentDirectoryInCache);
                }

                // Let's extract resource and copy it in cached folder
                try (InputStream resourceStream = resource.newInputStream()) {
                    Files.copy(resourceStream, extractedFileInCache, StandardCopyOption.REPLACE_EXISTING);
                    SAME_CACHED_FILE_CHECK_ON_PATH.putIfAbsent(resource.path(), Boolean.TRUE);
                }
            }

            // Checking if current cached resource has already been checked for compliance with non cached resource
            if (checkForCachedFileValidityNeededOn(resource)) {
                boolean sameFiles;
                try (InputStream cachedFileStream = Files.newInputStream(extractedFileInCache);
                     InputStream resourceStream = resource.newInputStream();) {
                    sameFiles = jewas.util.file.Files.sameStreams(cachedFileStream, resourceStream);
                }

                // If file differ, we should replace the cached one !
                if (!sameFiles) {
                    logger.info("Replacing outdated resource " + resource.path() + " in cache");
                    // Let's extract resource and copy it in cached folder
                    try (InputStream resourceStream = resource.newInputStream()) {
                        Files.copy(resourceStream, extractedFileInCache, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                SAME_CACHED_FILE_CHECK_ON_PATH.replace(resource.path(), Boolean.TRUE);
            }

            FileResponse fileResponse = request.respondFile();
            if (headers != null) {
                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    fileResponse.addHeader(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            fileResponse.file(extractedFileInCache);
        } catch (IOException e) {
            logger.error("Error opening :" + extractedFileInCache, e);
            request.respondError(HttpStatus.NOT_FOUND);
        }
    }
}
