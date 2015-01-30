package asynchttpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An Asynchronous HTTP Server implemented using NIO, 
 * allowing for multiple connections
 * 
 * @author craigchilds
 */
public class AsyncHTTPServer {
    
    private final AsynchronousServerSocketChannel server;
    private final SocketAddress addr;
    private final Future<AsynchronousSocketChannel> acceptFuture;
    private final AsynchronousChannelGroup group;
    private AsynchronousSocketChannel worker;
    private ByteBuffer buffer;
    private CompletionHandler handler;
    
    /**
     * Run an Async HTTP server on specified
     * address and port
     * 
     * @param address   The address to listen on
     * @param port      The port to listen on
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException 
     * @throws java.lang.ClassNotFoundException 
     */
    public AsyncHTTPServer(String address, int port) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException {
        addr = new InetSocketAddress(address, port);
        group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
        server = AsynchronousServerSocketChannel.open(group).bind(addr);
        acceptFuture = server.accept();
        listen();
    }
    
    private void listen() throws InterruptedException, ExecutionException, IOException, ClassNotFoundException {
        worker = acceptFuture.get();
        buffer = ByteBuffer.allocate(2048); // Assign 2KB
        handler = new HTTPReader(worker, buffer);
        worker.read(buffer, handler, handler);
        group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException {
        AsyncHTTPServer a = new AsyncHTTPServer("127.0.0.1", 1337);
    }
    
}