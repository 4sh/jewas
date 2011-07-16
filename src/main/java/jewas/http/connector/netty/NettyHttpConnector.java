package jewas.http.connector.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

import jewas.http.AddressAlreadyInUseException;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import jewas.http.HttpConnector;
import jewas.http.HttpRequest;
import jewas.http.RequestHandler;

public class NettyHttpConnector implements HttpConnector {
	private InetSocketAddress address;
	private List<RequestHandler> handlers = new CopyOnWriteArrayList<RequestHandler>();
	private ServerBootstrap bootstrap = null;

	@Override
	public void addHandler(RequestHandler requestHandler) {
		handlers.add(requestHandler);
	}
	
	@Override
	public void bind(int port) {
		address = new InetSocketAddress(port);
	}

	@Override
	public void start() {
		bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory(new RequestHandler() {
			@Override
			public void onRequest(HttpRequest request) {
				for (RequestHandler h : handlers) {
					h.onRequest(request);
				}
			}
		}));

		// Bind and start to accept incoming connections.
        try {
		    bootstrap.bind(address);
        }catch(ChannelException e){
            throw new AddressAlreadyInUseException("Netty server", address.getPort(), e);
        }

	}

    @Override
    public void stop(){
        bootstrap.releaseExternalResources();
    }



}
