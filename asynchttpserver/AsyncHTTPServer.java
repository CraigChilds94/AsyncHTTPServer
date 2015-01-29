/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    private AsynchronousServerSocketChannel server;
    private SocketAddress addr;
    private Future<AsynchronousSocketChannel> acceptFuture;
    private AsynchronousSocketChannel worker;
    private AsynchronousChannelGroup group;
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
     */
    public AsyncHTTPServer(String address, int port) throws IOException, InterruptedException, ExecutionException {
        addr = new InetSocketAddress(address, port);
        group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
        server = AsynchronousServerSocketChannel.open(group).bind(addr);
        acceptFuture = server.accept();
        bindWorker();
    }
    
    private void bindWorker() throws InterruptedException, ExecutionException {
        System.out.println("Binding worker");
        worker = acceptFuture.get();
        buffer = ByteBuffer.allocate(2048); // Assign 2KB
        handler = new HTTPReader();
        worker.read(buffer, buffer, handler);
        group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsyncHTTPServer a = new AsyncHTTPServer("127.0.0.1", 1337);
    }
    
}
