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
import org.jboss.netty.handler.codec.http.Attribute;
import org.jboss.netty.handler.codec.http.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.InterfaceHttpData;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.io.IOException;
import java.util.*;
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
	
	public DefaultHttpRequest(org.jboss.netty.handler.codec.http.HttpRequest request, HttpResponse response) {
		super();
		this.uri = request.getUri();
		this.headers = new Headers(request.getHeaders());
		this.response = response;
		
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
		path = queryStringDecoder.getPath();
        Map<String,List<String>> reqParameters = new HashMap<String, List<String>>(queryStringDecoder.getParameters());

        // Overriding method attribute if __httpMethod special parameter has been set
        // @see js/jewas-forms.js
        if("post".equalsIgnoreCase(request.getMethod().getName())
                && reqParameters.containsKey("__httpMethod")
                && !reqParameters.get("__httpMethod").isEmpty()){
            String overridenHttpMethod = reqParameters.get("__httpMethod").get(0);
            this.method = HttpMethod.valueOf(overridenHttpMethod);
            reqParameters.remove("__httpMethod");
        } else {
            this.method = HttpMethod.valueOf(request.getMethod().getName());
        }

        if("post".equalsIgnoreCase(request.getMethod().getName())
                || "put".equalsIgnoreCase(request.getMethod().getName())){
            try {
                HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(request);
                for(InterfaceHttpData d : postRequestDecoder.getBodyHttpDatas()){
                    if(d.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute){
                        Attribute att = (Attribute)d;
                        if(reqParameters.containsKey(att.getName())){
                            reqParameters.get(att.getName()).add(att.getValue());
                        } else {
                            List<String> paramValues = new ArrayList<String>();
                            paramValues.add(att.getValue());
                            reqParameters.put(att.getName(), paramValues);
                        }
                        // FIXME : Why is there a att.getFile() here whereas we aren't a FileUpload data type ???
                    } else if(d.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload){
                        // FIXME : implement fileupload here ...
                        // It could imply a refactoring in the Parameter object since it could
                        // contain not only Strings but Files to as values
                    }
                }
            } catch (Throwable t) {
                throw new RuntimeException("Error while reading POST request content : "+t.getMessage(), t);
            }
        }

        parameters = new Parameters(reqParameters);
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
		return new JsonResponse(this, response());
	}

    @Override
    public HtmlResponse respondHtml() {
        return new HtmlResponse(this, response());
    }

    @Override
    public FileResponse respondFile() {
        return new FileResponse(this, response());  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RedirectResponse redirect() {
        return new RedirectResponse(this, response());  //To change body of implemented methods use File | Settings | File Templates.
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
