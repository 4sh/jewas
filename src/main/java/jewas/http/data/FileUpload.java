package jewas.http.data;

import java.io.File;
import java.io.IOException;

/**
 * @author fcamblor
 * FileUpload representation for http forms
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

    public void renameTo(File dest) throws IOException {
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
