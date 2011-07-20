package jewas.http.connector.netty;

import jewas.http.ContentType;
import jewas.http.HttpMethod;
import jewas.http.HttpStatus;
import jewas.http.RequestHandler;
import jewas.http.impl.DefaultHttpRequest;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

	private final RequestHandler handler;
    private boolean readingChunks;
	private DefaultHttpRequest request;
	
	
    public HttpRequestHandler(RequestHandler handler) {
		this.handler = handler;
	}

	@Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (!readingChunks) {
            HttpRequest request = (HttpRequest) e.getMessage();

            if (is100ContinueExpected(request)) {
                send100Continue(e);
            }

            this.request = new DefaultHttpRequest(
            		HttpMethod.valueOf(request.getMethod().getName()), 
            		request.getUri(), 
            		request.getHeaders(), 
            		new jewas.http.HttpResponse() {
						private HttpResponse nettyResponse;
						
						@Override
						public jewas.http.HttpResponse status(HttpStatus status) {
							nettyResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status.code()));
							return this;
						}
						
						@Override
						public jewas.http.HttpResponse contentType(ContentType contentType) {
							nettyResponse.setHeader(CONTENT_TYPE, contentType.name());
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
                    });
            handler.onRequest(this.request);


            if (request.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = request.getContent();
                if (content.readable()) {
                	this.request.offerContent(content);
                }
                this.request.endContent();
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
                this.request.endContent();
                
//                HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
//                if (!trailer.getHeaderNames().isEmpty()) {
//                    buf.append("\r\n");
//                    for (String name: trailer.getHeaderNames()) {
//                        for (String value: trailer.getHeaders(name)) {
//                            buf.append("TRAILING HEADER: " + name + " = " + value + "\r\n");
//                        }
//                    }
//                    buf.append("\r\n");
//                }
            } else {
            	this.request.offerContent(chunk.getContent());
            }
        }
    }


    private void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
