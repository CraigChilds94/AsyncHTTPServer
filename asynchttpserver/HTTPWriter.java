package asynchttpserver;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 *
 * @author craigchilds
 */
public class HTTPWriter implements CompletionHandler<AsynchronousSocketChannel, CompletionHandler>{

    AsynchronousSocketChannel worker;
    ByteBuffer buffer;
    
    public HTTPWriter(AsynchronousSocketChannel worker, ByteBuffer buffer) {
        this.worker = worker;
        this.buffer = buffer;
    }
    
    @Override
    public void completed(AsynchronousSocketChannel id, CompletionHandler handler) {
        buffer.clear();
        worker.read(buffer, handler, handler);
    }

    @Override
    public void failed(Throwable exc, CompletionHandler handler) {
       System.out.println("Failed to read message");
    }
    
}
