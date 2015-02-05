package asynchttpserver;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 *
 * @author craigchilds
 */
public class WriteCompletion implements CompletionHandler<Object, Object>{

    AsynchronousSocketChannel worker;
    ByteBuffer buffer;
    
    public WriteCompletion(AsynchronousSocketChannel worker, ByteBuffer buffer) {
        this.worker = worker;
        this.buffer = buffer;
    }
    
    @Override
    public void completed(Object id, Object handler) {
        buffer.clear();
        worker.read(buffer, handler, (CompletionHandler)handler);

    }

    @Override
    public void failed(Throwable exc, Object handler) {
       System.out.println("Failed to read message");
    }
    
}
