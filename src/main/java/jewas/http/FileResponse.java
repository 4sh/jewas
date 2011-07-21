package jewas.http;


import jewas.util.file.Files;

import java.io.File;
import java.io.IOException;

public class FileResponse {
	private HttpResponse httpResponse;

	public FileResponse(HttpResponse response) {
		this.httpResponse = response;
	}

	public void file(File file) {
		httpResponse
			.status(HttpStatus.OK);
			//.contentType(new ContentType("text/html"));

        byte[] content = new byte[0];

        try {
            content = Files.getBytesFromFile(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        httpResponse.content(content);
    }
}
