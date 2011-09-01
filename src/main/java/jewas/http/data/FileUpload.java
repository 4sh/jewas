package jewas.http.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fcamblor
 * FileUpload representation for http forms
 * Not thread safe
 */
public class FileUpload extends NamedHttpData {

    private final List<org.jboss.netty.handler.codec.http.FileUpload> nettyFileUploadAdaptees =
            new ArrayList<org.jboss.netty.handler.codec.http.FileUpload>();

    public FileUpload(String name, List<org.jboss.netty.handler.codec.http.FileUpload> nettyFileUploads){
        super(name);
        this.nettyFileUploadAdaptees.addAll(nettyFileUploads);
    }

    public FileUpload(String name, org.jboss.netty.handler.codec.http.FileUpload nettyFileUpload){
        this(name, Arrays.asList(nettyFileUpload));
    }

    @Override
    public boolean isCompleted() {
        for(org.jboss.netty.handler.codec.http.FileUpload f : nettyFileUploadAdaptees){
            if(!f.isCompleted()){
                return false;
            }
        }
        return true;
    }

    public boolean isCompleted(int index) {
        return nettyFileUploadAdaptees.get(index).isCompleted();
    }

    /**
     * Immutable
     * Append a new file upload result to an existing file upload, and retrieve a defensive copy
     * of the resulting FileUpload
     * @param fileuploadToAdd The file upload result to add
     * @return A new defensive copy of current Fileupload, with given fileupload appended
     */
    public FileUpload append(FileUpload fileuploadToAdd){
        List<org.jboss.netty.handler.codec.http.FileUpload> fileUploads = new ArrayList<org.jboss.netty.handler.codec.http.FileUpload>(this.nettyFileUploadAdaptees);
        fileUploads.addAll(fileuploadToAdd.nettyFileUploadAdaptees);
        return new FileUpload(this.name(), fileUploads);
    }

    /**
     * Expose data into given file
     * Won't be time consuming unless dest file is on the same filesystem as temporary
     * files filesystem
     * With certain implementations, this method will be mutable for the
     * current FileUpload
     * @param index Index of file upload parameter
     * @param dest Destination file
     * @throws IOException
     */
    public void toFile(int index, File dest) throws IOException {
        this.nettyFileUploadAdaptees.get(index).renameTo(dest);
    }

    /**
     * Expose first uploaded data parameter into given file
     * Won't be time consuming unless dest file is on the same filesystem as temporary
     * files filesystem
     * With certain implementations, this method will be mutable for the
     * current FileUpload
     * @param dest
     * @throws IOException
     */
    public void toFile(File dest) throws IOException {
        toFile(0, dest);
    }

    public int count(){
        return nettyFileUploadAdaptees.size();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FileUpload");
        sb.append('{');
        sb.append("name='").append(name).append('\'');
        sb.append("file='").append(nettyFileUploadAdaptees.toString()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
