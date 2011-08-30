package jewas.http.util;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.jboss.netty.handler.codec.http.FileUpload;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author fcamblor
 */
public class HttpTestUtils {
    public static class FileUploadDescriptor{
        private File file;
        private String mimeType;
        public FileUploadDescriptor(File file, String mimeType){
            this.file = file;
            this.mimeType = mimeType;
        }
        public FileUploadDescriptor file(File _file){
            this.file = _file;
            return this;
        }
        public File file(){
            return this.file;
        }
        public FileUploadDescriptor mimeType(String _mimeType){
            this.mimeType = _mimeType;
            return this;
        }
        public String mimeType(){
            return this.mimeType;
        }
    }

    public static String sendMultipartFormTo(String url, Map<String, FileUploadDescriptor> filesToUpload, Map<String,String> parameters) throws IOException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
        HttpVersion.HTTP_1_1);

        HttpPost post = new HttpPost( url );
        MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

        // For File parameters
        for(Map.Entry<String,FileUploadDescriptor> e : filesToUpload.entrySet()){
            entity.addPart(e.getKey(), new FileBody(e.getValue().file(), e.getValue().mimeType()));
        }

        // For usual String parameters
        for(Map.Entry<String,String> e : parameters.entrySet()){
            entity.addPart( e.getKey(), new StringBody( e.getValue(), "text/plain", Charset.forName("UTF-8")));
        }

        post.setEntity(entity);

        // Here we go!
        String response = EntityUtils.toString(client.execute(post).getEntity(), "UTF-8");

        return response;
    }
}
