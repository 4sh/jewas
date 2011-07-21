package jewas.util.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 20/07/11
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class Files {
    /**
     * Get a file from a given path.
     * @param path the path
     * @return the file at the given path
     * @throws FileNotFoundException a {@link FileNotFoundException}
     */
    public static File getFileFromPath(String path) throws FileNotFoundException {
        if (path == null) {
            throw new FileNotFoundException("The given path is null.");
        }

        URL resource = Files.class.getClassLoader().getResource(path);

        if (resource == null) {
            throw new FileNotFoundException("The path: " + path + " was not found in the classpath.");
        }

        String resourcePath = resource.getPath();

        File file = new File(resourcePath);

        if (!file.exists()) {
            throw new FileNotFoundException("The file at the path: " + resourcePath + " was not found. The path you give was: " + path);
        }

        return file;
    }

    /**
     * Get the bytes array that correspond to the given file.
     * @param file a file
     * @return the bytes array that correspond to the given file
     * @throws IOException an {@link IOException}
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        if (file == null) {
            throw new IOException("File is null");
        }

        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            is.close();
            throw new IOException("The file to load is too large.");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();

        return bytes;
    }

    /**
     * Get the string that correspond to the content of the given file.
     * @param file the {@link File}
     * @return the string that correspond to the content of the given file.
     */
    public static String getStringFromFile(File file) {
        if (file == null || !file.canRead()) {
            return "";
        }

        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream f = null;

        try {
            f = new BufferedInputStream(new FileInputStream(file));
            f.read(buffer);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (f != null) try { f.close(); } catch (IOException ignored) { }
        }
        
        return new String(buffer);
    }
}
