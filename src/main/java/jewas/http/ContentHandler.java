package jewas.http;

import org.jboss.netty.buffer.ChannelBuffer;

public interface ContentHandler {
	public void onContentAvailable(HttpRequest request, ChannelBuffer content);
	public void onContentEnd(HttpRequest request);
}
