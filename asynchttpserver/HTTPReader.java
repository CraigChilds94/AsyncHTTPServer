package asynchttpserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author craigchilds
 */
public class HTTPReader implements CompletionHandler<Integer, CompletionHandler>{
    
    private AsynchronousSocketChannel worker;
    private ByteBuffer buffer;
        
    public HTTPReader(AsynchronousSocketChannel worker, ByteBuffer buffer) {
        this.worker = worker;
        this.buffer = buffer;
    }
    
    @Override
    public void completed(Integer id, CompletionHandler handler) {
        // Handle any writing to this socket
        CompletionHandler writeHandler = new HTTPWriter(worker, buffer);
      
        // Check to see if the socket has been closed
        if (id == -1) {
            System.err.println("Socket has been closed");
            try {
                worker.close();
            } catch (IOException ex) {
                Logger.getLogger("HTTPReader").log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }
        
        // We want to flip the buffer so we can use it again
        buffer.flip();
        
        // Write out the data to the client
        worker.write(buffer, handler, writeHandler);
    }

    @Override
    public void failed(Throwable exc, CompletionHandler handler) {
       System.out.println("Failed to read message");
    }
    
}
