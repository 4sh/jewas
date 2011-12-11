package jewas.http;


import jewas.http.impl.DefaultHttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileResponse {
	private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private ContentType contentType;
    /**
     * Pattern used to get value of 'Range' HTTP header.
     */
    private static final Pattern SINGLE_BYTE_RANGE = Pattern.compile("bytes=(\\d+)?-(\\d+)?");


	public FileResponse(DefaultHttpRequest request, HttpResponse response) {
        this.httpRequest = request;
        this.httpResponse = response;
        this.httpResponse.status(HttpStatus.OK);
        this.contentType = ContentType.guessContentTypeByUri(request.uri());
        if(contentType == null){
            contentType = ContentType.TXT_PLAIN;
        }
	}

	public void file(Path path) {
        httpResponse.contentType(contentType);

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(path.toFile(), "r");
            if (maybeServeRange(httpRequest, raf.getChannel(), httpResponse)) {
                return;
            }
            httpResponse.content(path);
            httpResponse.addHeader("Content-Length", remaining(raf.getChannel()));
        } catch (FileNotFoundException fnfe) {
            httpResponse.status(HttpStatus.NOT_FOUND).content("Not found: " + path);
        }
    }

    public FileResponse contentType(ContentType _contentType){
        this.contentType = _contentType;
        return this;
    }

    private boolean maybeServeRange(HttpRequest request, FileChannel contents, HttpResponse response) {
        String range = request.headers().getHeaderValue("Range");
        if (null == range) {
            return false;
        }

        Matcher matcher = SINGLE_BYTE_RANGE.matcher(range);
        if (!matcher.matches()) {
            return false;
        }
        String startString = matcher.group(1);
        String endString = matcher.group(2);
        if (null != startString && null != endString) {
            int start = Integer.parseInt(startString);
            int end = Integer.parseInt(endString);
            if (start <= end) {
                serveRange(start,
                           Math.min(remaining(contents) - 1, end),
                           contents,
                           response);
                return true;
            }
        } else if (null != startString) {
            serveRange(Integer.parseInt(startString),
                       remaining(contents) - 1,
                       contents,
                       response);
            return true;
        } else if (null != endString) {
            int end = Integer.parseInt(endString);
            serveRange(remaining(contents) - end,
                       remaining(contents) - 1,
                       contents,
                       response);
            return true;
        }
        return false;
    }

    private long remaining(FileChannel channel) {
        if (channel == null) {
            return 0;
        }
        try {
            return channel.size() - (1 + channel.position());
        } catch (IOException e) {
            return 0;
        }
    }

    /*
    private boolean hasRemaining(FileChannel channel) {
        return remaining(channel) != 0;
    }*/

    private void serveRange(long start, long end, FileChannel contents, HttpResponse response) {
        if (start > remaining(contents)) {
            response.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .addHeader("Content-Range", "bytes */" + remaining(contents));
            return;
        }
        response.status(HttpStatus.PARTIAL_CONTENT)
                .addHeader("Content-Length", end - start + 1) // since its inclusive
                .addHeader("Content-Range",
                        "bytes " + start + "-" + end + "/" + remaining(contents));
        try {
           /* contents.limit(contents.position() + end + 1)
                    .position(contents.position() + start);*/

            long position = contents.position() + start;
            long count = contents.position() + end + 1 - start;
            File f = File.createTempFile("tmp", "");
            RandomAccessFile chunk = new RandomAccessFile(f, "rw");
            contents.transferTo(position, count, chunk.getChannel());
            response.content(Paths.get(f.getPath()));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
