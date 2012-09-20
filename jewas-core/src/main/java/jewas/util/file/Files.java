package jewas.util.file;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 20/07/11
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class Files {

    /**
     * Get a file from the same archive as Files.class file
     * @param path the path
     * @return the inputstream at the given path
     * @throws IOException a {@link IOException}
     * @see Files#getInputStreamFromPath(Class, String)
     */
    public static InputStream getInputStreamFromPath(String path) throws IOException {
        return getInputStreamFromPath(Files.class, path);
    }

    /**
     * Get a inputstream from a given path inside the same archive where resides the given clazz
     * @param clazz The classloader we will look for path file
     * @param path the path
     * @return the inputstream at the given path
     * @throws IOException a {@link IOException}
     */
    public static InputStream getInputStreamFromPath(Class clazz, String path) throws IOException {
        return getResourceFromPath(clazz, path).openStream();
    }

    /**
     * Get an resource from a given path inside the same archive where resides the given clazz
     * @param clazz The classloader we will look for path file
     * @param path the path
     * @return the inputstream at the given path
     * @throws IOException a {@link IOException}
     */
    public static URL getResourceFromPath(Class clazz, String path) throws IOException {
        if (path == null) {
            throw new FileNotFoundException("The given path is null.");
        }

        Enumeration<URL> resources = clazz.getClassLoader().getResources(path);
        if(!resources.hasMoreElements()){
            throw new FileNotFoundException("The path: " + path + " was not found in the classpath.");
        }

        URL resource = resources.nextElement();

        // If there is more than 1 resource matching path in the classpath ...
        if(resources.hasMoreElements()){
            URL currentResource = resource;
            resource = null;
            // Trying to find the first resource matching path _and_ where resides clazz
            while(currentResource != null){

                String resourcePath = currentResource.toString();
                StringBuilder classPath = new StringBuilder();
                classPath.append(resourcePath.substring(0, resourcePath.length() - path.length()))
                         .append(clazz.getCanonicalName().replaceAll("\\.", "/"))
                         .append(".class");

                URL classUrl = new URL(classPath.toString());
                boolean classFoundInClasspath = false;
                try (InputStream  tmpStream = classUrl.openStream())
                {
                    classFoundInClasspath = true;
                } catch(IOException e){
                    classFoundInClasspath = false;
                } 

                if(classFoundInClasspath){
                    resource = currentResource;
                    break;
                }

                if(!resources.hasMoreElements()){
                    currentResource = null;
                } else {
                    currentResource = resources.nextElement();
                }
            }
        }

        if (resource == null) {
            throw new FileNotFoundException("The path: " + path + " was not found in the classpath.");
        }

        return resource;
    }

    /**
     * Get the bytes array that correspond to the given file.
     * @param stream a stream
     * @return the bytes array that correspond to the given file
     * @throws IOException an {@link IOException}
     */
    public static byte[] getBytesFromStream(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IOException("Stream is null");
        }

        // Create the byte array to hold the data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] chunks = new byte[1024];

        int bytesRead = -1;
        while((bytesRead = stream.read(chunks)) == 1024){
            baos.write(chunks);
        }

        if(bytesRead != -1){
            baos.write(chunks, 0, bytesRead);
        }

        // Close the input stream and return bytes
        stream.close();

        return baos.toByteArray();
    }

    public static long copyStreamTo(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        long count = 0;
        int bytesRead = 0;
        while(-1 != (bytesRead = in.read(buf))) {
            out.write(buf, 0, bytesRead);
            count += bytesRead;
        }
        return count;
    }

    /**
     * Get the string that correspond to the content of the given file.
     * @param stream the {@link InputStream}
     * @return the string that correspond to the content of the given file.
     */
    public static String getStringFromStream(InputStream stream) throws IOException {
        return new String(getBytesFromStream(stream));
    }

    /**
     * Will create any intermediate non existing directory leading to f
     */
    public static void touchFileWithParents(File f) throws IOException {
        Stack<File> filesToCreate = new Stack<File>();
        File currentFile = f;
        while(!currentFile.exists()){
            filesToCreate.push(currentFile);
            currentFile = currentFile.getParentFile();
        }
        while(!filesToCreate.empty()){
            currentFile = filesToCreate.pop();
            if(filesToCreate.empty()){ // Last chunk of path : it will be a file
                currentFile.createNewFile();
            } else { // otherwise, it will be a folder
                currentFile.mkdir();
            }
        }
    }

    /**
     * Returns the extension of the given file name if it has one, return the empty string otherwise.
     *
     * @param filename the string from which to extract the file extension.
     * @return a non null string containing the file extension if found, empty string otherwise.
     */
    public static String getFileExtension(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Cannot get extension from null filename.");
        }
        String[] fileNameParts = filename.split("\\.");
        if (fileNameParts == null || fileNameParts.length == 0 || fileNameParts.length == 1) {
            return "";
        }
        return fileNameParts[fileNameParts.length - 1];
    }
}
