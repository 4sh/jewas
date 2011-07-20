package jewas.http;


import jewas.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
            content = FileUtil.getBytesFromFile(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        httpResponse.content(content);
    }
}
