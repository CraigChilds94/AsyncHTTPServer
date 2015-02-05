package asynchttpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
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
    
    private AsynchronousServerSocketChannel server;
    private SocketAddress addr;
    private Future<AsynchronousSocketChannel> acceptFuture;
    private AsynchronousChannelGroup group;
    private AsynchronousSocketChannel client;
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
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        acceptFuture = server.accept();
        listen();
    }
    
    private void listen() throws InterruptedException, ExecutionException, IOException, ClassNotFoundException {
        client = acceptFuture.get();
        buffer = ByteBuffer.allocate(4096); // Assign 2KB
        handler = new ReadCompletion(client, buffer);
        client.read(buffer, handler, handler);
        System.out.println("\nREQUEST\n");
        System.out.println(new String(buffer.array()).trim());
        System.out.println("\nRESPONSE\n");
//        group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException {
        AsyncHTTPServer a = new AsyncHTTPServer("localhost", 1337);
    }
    
}