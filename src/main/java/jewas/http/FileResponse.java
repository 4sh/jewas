package jewas.http;


import jewas.util.file.Files;

import java.io.IOException;
import java.io.InputStream;

public class FileResponse {
	private HttpResponse httpResponse;

	public FileResponse(HttpResponse response) {
		this.httpResponse = response;
	}

	public void file(InputStream stream) {
		httpResponse
			.status(HttpStatus.OK);
			//.contentType(new ContentType("text/html"));

        byte[] content = new byte[0];

        try {
            content = Files.getBytesFromStream(stream);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        httpResponse.content(content);
    }
}
