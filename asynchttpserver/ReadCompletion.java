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
public class ReadCompletion implements CompletionHandler<Object, Object>{
    
    private AsynchronousSocketChannel worker;
    private ByteBuffer buffer;
        
    public ReadCompletion(AsynchronousSocketChannel worker, ByteBuffer buffer) {
        this.worker = worker;
        this.buffer = buffer;
    }
    
    @Override
    public void completed(Object id, Object handler) {
        // Handle any writing to this socket
        CompletionHandler writeHandler = new WriteCompletion(worker, buffer);
        
        // Check to see if the socket has been closed
        if ((Integer)id == -1) {
            System.err.println("Socket has been closed");
            try {
                worker.write(buffer, handler, writeHandler);
                worker.close();
            } catch (IOException ex) {
                Logger.getLogger("HTTPReader").log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }
        
        // We want to flip the buffer so we can use it again
        buffer.flip();
        
        // Send something as a response
        String response = "status: 200 OK\n" + "version: HTTP/1.1\n\n";
        ByteBuffer responseBuffer = ByteBuffer.allocate(4096);
        responseBuffer.put(response.getBytes());
        
        System.out.println(new String(responseBuffer.array()).trim());
        
        // Write out the data to the client
        worker.write(responseBuffer);
       
        try {
            worker.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadCompletion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void failed(Throwable exc, Object handler) {
       System.out.println("Failed to read message");
    }
    
}
