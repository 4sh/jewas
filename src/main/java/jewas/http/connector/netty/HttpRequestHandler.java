package jewas.http.connector.netty;

import jewas.http.ContentType;
import jewas.http.HttpStatus;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.data.NamedString;
import jewas.http.impl.DefaultHttpRequest;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.*;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

	private final RequestHandler handler;
    private final List<jewas.http.data.HttpData> contentData = new ArrayList<jewas.http.data.HttpData>();

    private volatile boolean readingChunks;

	private DefaultHttpRequest request;

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(
            DefaultHttpDataFactory.MINSIZE); // Disk if size exceed MINSIZE

    private HttpPostRequestDecoder decoder = null;
    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
                                                         // on exit (in normal
                                                         // exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
                                                        // exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }
	
    public HttpRequestHandler(RequestHandler handler) {
		this.handler = handler;
	}

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        // Have we chunked previous response ?
        if (!readingChunks) {
            // clean previous FileUpload if Any
            if (decoder != null) {
                decoder.cleanFiles();
                decoder = null;
            }

            HttpRequest request = (HttpRequest) e.getMessage();

            // headers
            List<Map.Entry<String, String>> headers = request.getHeaders();

            // cookies
            Set<Cookie> cookies;
            String value = request.getHeader(HttpHeaders.Names.COOKIE);
            if (value == null) {
                cookies = Collections.emptySet();
            } else {
                CookieDecoder decoder = new CookieDecoder();
                cookies = decoder.decode(value);
            }

            // uri params
            QueryStringDecoder decoderQuery = new QueryStringDecoder(request
                    .getUri());
            Map<String, List<String> > uriAttributes = decoderQuery
                    .getParameters();

            // Overriding method attribute if __httpMethod special parameter has been set
            // @see js/jewas-forms.js
            String methodName = null;
            if("post".equalsIgnoreCase(request.getMethod().getName())
                    && uriAttributes.containsKey("__httpMethod")
                    && !uriAttributes.get("__httpMethod").isEmpty()){
                String overridenHttpMethod = uriAttributes.get("__httpMethod").get(0);
                methodName = overridenHttpMethod;
                uriAttributes.remove("__httpMethod");
            } else {
                methodName = request.getMethod().getName();
            }

            this.request = new DefaultHttpRequest(
                    request.getUri(),
                    methodName,
                    headers,
                    cookies,
                    uriAttributes,
                    decoderQuery.getPath(),
            		new jewas.http.HttpResponse() {
						private HttpResponse nettyResponse;

						@Override
						public jewas.http.HttpResponse status(HttpStatus status) {
							nettyResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status.code()));
							return this;
						}

						@Override
						public jewas.http.HttpResponse contentType(ContentType contentType) {
							nettyResponse.setHeader(CONTENT_TYPE, contentType.value());
							return this;
						}

						@Override
						public jewas.http.HttpResponse content(String content) {
							return content(content.getBytes(CharsetUtil.UTF_8));
						}

                        @Override
                        public jewas.http.HttpResponse content(byte[] content) {
                            nettyResponse.setContent(ChannelBuffers.copiedBuffer(content));

							ChannelFuture future = e.getChannel().write(nettyResponse);
							future.addListener(ChannelFutureListener.CLOSE);
							return this;
                        }

                        @Override
                        public jewas.http.HttpResponse addHeader(String header, Object value) {
                            nettyResponse.addHeader(header, value);
                            return this;
                        }
                    },
                    // TODO: to remove !!!
                    request.getContent().toByteBuffer());

            this.handler.onRequest(this.request);

            // if GET Method: should not try to create a HttpPostRequestDecoder
            try {
                decoder = new HttpPostRequestDecoder(factory, request);
            } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                // TODO : retrieve writeResponse from netty source code ???
                //writeResponse(e.getChannel());
                Channels.close(e.getChannel());
                throw new RuntimeException("Error while decoding post request", e1);
            } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException e1) {
                // GET Method: should not try to create a HttpPostRequestDecoder
                // So OK but stop here
                // TODO : retrieve writeResponse from netty source code ???
                //writeResponse(e.getChannel());
                endOfReadContent(BodyParameters.Types.EMPTY);
                return;
            }

            // If we are dealing with a chunked request, we are on a multipart/form-data
            // so let's wait for the next chunk before processing the request
/*            if(request.isChunked()){
                send100Continue(e);

                // Don' create DefaultHttpRequest since when chunked, content cannot be processed
                return;
                // TODO implement streaming on this type of request
                // You will encounter problem when doing a postRequestDecoder.getBodyHttpDatas()
                // in the DefaultHttpRequest constructor since netty won't be able to process body
                // content unless it is completely available
            }
*/

            if (request.isChunked()) {
                // Chunk version
                readingChunks = true;
            } else {
                // Not chunk version
                readHttpDataAllReceive(e.getChannel());
                // TODO : retrieve writeResponse from netty source code ???
                //writeResponse(e.getChannel());
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            try {
                decoder.offer(chunk);
            } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                // TODO : retrieve writeResponse from netty source code ???
                //writeResponse(e.getChannel());
                Channels.close(e.getChannel());
                throw new RuntimeException("Error while decoding post request", e1);
            }

            // Reading chunk by chunk (minimize memory usage due to Factory)
            readHttpDataChunkByChunk(e.getChannel());
            // I think this part is now useless since we offer HttpData in writeHttpData
            request.offerContent(chunk.getContent());

            if (chunk.isLast()) {
                // Useless .. is there an error in netty example ???
                //readHttpDataAllReceive(e.getChannel());
                // TODO : retrieve writeResponse from netty source code ???
                //writeResponse(e.getChannel());
                readingChunks = false;
            }
        }
    }

        /**
     * Example of reading all InterfaceHttpData from finished transfer
     *
     * @param channel
     */
    private void readHttpDataAllReceive(Channel channel) {
        List<InterfaceHttpData> datas = null;
        try {
            datas = decoder.getBodyHttpDatas();
        } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e1) {
            // TODO : retrieve writeResponse from netty source code ???
            //writeResponse(channel);
            Channels.close(channel);
            return;
        }
        for (InterfaceHttpData data: datas) {
            writeHttpData(data);
        }
        // TODO: manage streamed body parameters
        endOfReadContent(BodyParameters.Types.FORM);
    }

    /**
     * Example of reading request by chunk and getting values from chunk to
     * chunk
     *
     * @param channel
     */
    private void readHttpDataChunkByChunk(Channel channel) {
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    // new value
                    writeHttpData(data);
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
            // End of content
            // TODO: manage streamed body parameters
            endOfReadContent(BodyParameters.Types.FORM);
            return;
        }
    }

    private void writeHttpData(InterfaceHttpData data) {
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                // Error while reading data from File, only print name and error
                e1.printStackTrace();
                System.out.println("\r\nBODY Attribute: " +
                        attribute.getHttpDataType().name() + ": " +
                        attribute.getName() + " Error while reading value: " +
                        e1.getMessage() + "\r\n");
                return;
            }
            // TODO : Manage NamedString with multiple values for content parameters ???
            NamedString stringData = new NamedString(data.getName(), value);
            this.handler.offer(this.request, stringData);
            this.contentData.add(stringData);
        } else {
            System.out.println("\r\nBODY FileUpload: " +
                    data.getHttpDataType().name() + ": " + data.toString() +
                    "\r\n");
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                jewas.http.data.FileUpload jewasFileupload = new jewas.http.data.FileUpload(data.getName(), fileUpload);
                this.handler.offer(this.request, jewasFileupload);
                if (fileUpload.isCompleted()) {
                    this.contentData.add(jewasFileupload);
                    /*
                    if (fileUpload.length() < 10000) {
                        System.out.println("\tContent of file\r\n");
                        try {
                            System.out.println(((FileUpload) data)
                                            .getString(((FileUpload) data)
                                                    .getCharset()));
                        } catch (IOException e1) {
                            // do nothing for the example
                            e1.printStackTrace();
                        }
                        System.out.println("\r\n");
                    } else {
                        System.out.println("\tFile too long to be printed out:" +
                                        fileUpload.length() + "\r\n");
                    }
                    */
                    // fileUpload.isInMemory();// tells if the file is in Memory
                    // or on File
                    // fileUpload.renameTo(dest); // enable to move into another
                    // File dest
                    // decoder.removeFileUploadFromClean(fileUpload); //remove
                    // the File of to delete file
                } else {
                    System.out.println("\tFile to be continued but should not!\r\n");
                }
            }
        }
    }

    private void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    private void endOfReadContent(BodyParameters.Types bodyParameterType){
        handler.onReady(this.request, bodyParameterType.createBodyParameters(contentData));
        // Notifying request that end of content is reached
        // Wondering if it is still useful...
        request.endContent();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
