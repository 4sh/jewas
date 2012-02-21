package fr.fsh.bbeeg.common.persistence;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import jewas.http.data.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author driccio
 */
public class TempFiles {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(TempFiles.class);

    private static final String TMP_FILE_PREFIX = "tmp_";

    private static Integer generateId() {
        return new Double(Math.random() * Integer.MAX_VALUE).intValue();
    }

    public static String store(FileUpload fileUpload, String extension) {
        Integer id = generateId();
        String fileName = TMP_FILE_PREFIX + id + "." + extension;
        String url = BBEEGConfiguration.INSTANCE.cliOptions().tmpContentFileRepository() + fileName;
        Path path = Paths.get(url);

         try {
             fileUpload.toFile(path.toFile());
         } catch (IOException e) {
             e.printStackTrace();
         }

        return fileName;
    }

    public static String store(String text) {
        Integer id = generateId();
        String fileName = TMP_FILE_PREFIX + id + ".txt";
        String url = BBEEGConfiguration.INSTANCE.cliOptions().tmpContentFileRepository() + fileName;
        Path path = Paths.get(url);

        try {
            Files.write(path, text.getBytes());
        } catch (IOException e) {
            logger.error("Cannot write temp file {}", url);
        }

        return fileName;
    }

    public static Path getPath(String fileId) {
        return Paths.get(BBEEGConfiguration.INSTANCE.cliOptions().tmpContentFileRepository() + fileId);
    }

    public static void removeFiles(String[] fileNames) {
        for (String fileName : fileNames) {
            removeFile(fileName);
        }
    }

    public static void removeFile(String fileName) {
        if (tmpFileExists(fileName)) {
            Path filePath = getPath(fileName);

            try {
                Files.delete(filePath);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

     public static boolean tmpFileExists(String fileName) {
        return Files.exists(getPath(fileName));
    }
}
