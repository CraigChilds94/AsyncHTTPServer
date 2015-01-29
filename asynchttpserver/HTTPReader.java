/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asynchttpserver;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 *
 * @author craigchilds
 */
public class HTTPReader implements CompletionHandler<Integer, ByteBuffer>{

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("Received message with code : " + result);
        String message = new String(attachment.array());
        System.out.println(message);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
       System.out.println("Failed to read message");
    }
    
}
