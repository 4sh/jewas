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
public class FileUpload extends NamedHttpData<org.jboss.netty.handler.codec.http.FileUpload> {

    public FileUpload(String name, List<org.jboss.netty.handler.codec.http.FileUpload> nettyFileUploads){
        super(name, nettyFileUploads);
    }

    public FileUpload(String name, org.jboss.netty.handler.codec.http.FileUpload nettyFileUpload){
        super(name, nettyFileUpload);
    }

    @Override
    protected NamedHttpData<org.jboss.netty.handler.codec.http.FileUpload> newInstance(String name, List<org.jboss.netty.handler.codec.http.FileUpload> values) {
        return new FileUpload(name, values);
    }

    @Override
    public boolean isCompleted() {
        for(org.jboss.netty.handler.codec.http.FileUpload f : values){
            if(!f.isCompleted()){
                return false;
            }
        }
        return true;
    }

    public boolean isCompleted(int index) {
        return values.get(index).isCompleted();
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
        this.values.get(index).renameTo(dest);
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
}
