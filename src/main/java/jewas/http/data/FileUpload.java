package jewas.http.data;

import java.io.File;
import java.io.IOException;

/**
 * @author fcamblor
 * FileUpload representation for http forms
 * Not thread safe
 */
public class FileUpload extends NamedHttpData {

    private final org.jboss.netty.handler.codec.http.FileUpload nettyFileUploadAdaptee;

    public FileUpload(String name, org.jboss.netty.handler.codec.http.FileUpload nettyFileUpload){
        super(name);
        this.nettyFileUploadAdaptee = nettyFileUpload;
    }

    @Override
    public boolean isCompleted() {
        return nettyFileUploadAdaptee.isCompleted();
    }

    /**
     * Expose data into given file
     * Won't be time consuming unless dest file is on the same filesystem as temporary
     * files filesystem
     * With certain implementations, this method will be mutable for the
     * current FileUpload
     * @param dest
     * @throws IOException
     */
    public void toFile(File dest) throws IOException {
        this.nettyFileUploadAdaptee.renameTo(dest);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FileUpload");
        sb.append('{');
        sb.append("name='").append(name).append('\'');
        try {
            sb.append("file='").append(nettyFileUploadAdaptee.getFile().getAbsolutePath()).append('\'');
        } catch (IOException e) {
            sb.append("file=").append("[Problem : ").append(e.getMessage());
        }
        sb.append('}');
        return sb.toString();
    }
}
