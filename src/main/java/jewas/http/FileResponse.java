package jewas.http;


import jewas.http.impl.DefaultHttpRequest;
import jewas.util.file.Files;

import java.io.IOException;
import java.io.InputStream;

public class FileResponse {
	private HttpResponse httpResponse;
    private ContentType contentType;

	public FileResponse(DefaultHttpRequest request, HttpResponse response) {
		this.httpResponse = response;
        this.httpResponse.status(HttpStatus.OK);
        contentType = ContentType.guessContentTypeByUri(request.uri());
        if(contentType ==null){
            contentType = ContentType.TXT_PLAIN;
        }
	}

	public void file(InputStream stream) {
        httpResponse.contentType(contentType);
        byte[] content = new byte[0];

        try {
            content = Files.getBytesFromStream(stream);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        httpResponse.content(content);
    }

    public FileResponse contentType(ContentType _contentType){
        this.contentType = _contentType;
        return this;
    }
}
