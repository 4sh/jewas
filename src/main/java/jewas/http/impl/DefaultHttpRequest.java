package jewas.http.impl;

import jewas.http.ContentHandler;
import jewas.http.FileResponse;
import jewas.http.Headers;
import jewas.http.HtmlResponse;
import jewas.http.HttpMethod;
import jewas.http.HttpRequest;
import jewas.http.HttpResponse;
import jewas.http.HttpStatus;
import jewas.http.JsonResponse;
import jewas.http.Parameters;
import jewas.http.RedirectResponse;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultHttpRequest implements HttpRequest {
	private final HttpMethod method;
	private final String uri;
	private final Headers headers;

	// computed fields
	private final String path;
	private final Parameters parameters;
	

	// fields which state is mutable
	private final HttpResponse response;
	private final List<ContentHandler> handlers = new CopyOnWriteArrayList<ContentHandler>();
	
	public DefaultHttpRequest(HttpMethod method, String uri,
			List<Entry<String, String>> headers, HttpResponse response) {
		super();
		this.method = method;
		this.uri = uri;
		this.headers = new Headers(headers);
		this.response = response;
		
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
		path = queryStringDecoder.getPath();
		parameters = new Parameters(queryStringDecoder.getParameters());
	}
	
	@Override
	public HttpRequest addContentHandler(ContentHandler h) {
		handlers.add(h);
		return this;
	}

	public void endContent() {
		for (ContentHandler h : handlers) {
			h.onContentEnd(this);
		}
	}

	public void offerContent(ChannelBuffer content) {
		for (ContentHandler h : handlers) {
			h.onContentAvailable(this, content);
		}
	}

     // TODO: Why not having a method renderJson(object) like in Play!
	@Override
	public JsonResponse respondJson() {
		return new JsonResponse(response());
	}

    @Override
    public HtmlResponse respondHtml() {
        return new HtmlResponse(response());
    }

    @Override
    public FileResponse respondFile() {
        return new FileResponse(response());  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RedirectResponse redirect() {
        return new RedirectResponse(response());  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void respondError(HttpStatus status) {
        // TODO: improve this error handling
        // For example, by giving, in dev mode, every available routes
		response().status(status).content("No route found for your path <"+this.path()+">");
	}

	private HttpResponse response() {
		return response;
	}

	public HttpMethod method() {
		return method;
	}

	public String uri() {
		return uri;
	}

	public Headers headers() {
		return headers;
	}

	public String path() {
		return path;
	}

	public Parameters parameters() {
		return parameters;
	}
	
	
	
	
//	public boolean isKeepAlive() {
//        String connection = getHeader(Names.CONNECTION);
//        if (Values.CLOSE.equalsIgnoreCase(connection)) {
//            return false;
//        }
//
//        if (protocolVersion.isKeepAliveDefault()) {
//            return !Values.CLOSE.equalsIgnoreCase(connection);
//        } else {
//            return Values.KEEP_ALIVE.equalsIgnoreCase(connection);
//        }
//    }
}
