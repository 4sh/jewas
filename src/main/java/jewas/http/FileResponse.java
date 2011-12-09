package jewas.http;


import jewas.http.impl.DefaultHttpRequest;

import java.nio.file.Path;

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

	public void file(Path path) {
        httpResponse.contentType(contentType);

        httpResponse.content(path);
    }

    public FileResponse contentType(ContentType _contentType){
        this.contentType = _contentType;
        return this;
    }
}
