
package com.stxnext.management.android.games.poker.multiplayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import android.os.Handler;
import android.util.Log;

import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.NotificationFor;

public class NIOConnectionHandler implements ConnectionHandler{

    ConnectionThread connectionThread;
    Handler handler;
    boolean connected;
    Socket socket;
    PrintWriter out;
    ReadThread readThread;
    volatile boolean requestAwaitingForResponse;
    NIOConnectionHandlerCallbacks callbacks;
    
    private Queue<MessageWrapper> requestQueue;
    // TODO : prepare queue guarding thread that checks if socket is not clogged which may happen in simultaneout both direction streaming 
    // also server could just don't respond
    public NIOConnectionHandler(NIOConnectionHandlerCallbacks callbacks){
        handler = new Handler();
        requestQueue = new ArrayBlockingQueue<MessageWrapper>(200);
        this.callbacks = callbacks;
    }
    
    public void enqueueRequest(RequestFor request, AbstractMessage message){
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_REQUEST, request.getMessage(), message.serialize());
        if(requestAwaitingForResponse){
            requestQueue.add(wrapper);
        }
        else{
            dispatchRequest(wrapper);
        }
    }
    
    private void dispatchRequest(MessageWrapper wrapper){
        if(wrapper == null)
            return;
        
        requestAwaitingForResponse = true;
        String outString = wrapper.serialize();
        out.write(outString+"\r\n");
        out.flush();
    }
    
    public void start(){
        if(connectionThread != null){
            connectionThread.interrupt();
        }
        connectionThread = new ConnectionThread();
        connectionThread.start();
    }

    
    private class ReadThread extends Thread {
        BufferedReader in;
        public ReadThread(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {

            while (socket != null && socket.isConnected()) {
                try {
                    final String line = in.readLine();

                    if (line == null)
                        continue;
                    requestAwaitingForResponse = false;
                    dispatchRequest(requestQueue.poll());

                    MessageWrapper wrapper = MessageWrapper.fromJsonString(line,
                            MessageWrapper.class);
                    if (wrapper != null) {
                        unwrapMessageAndPassOn(wrapper);
                    }

                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            }

            Log.e("", "socket is closed");
        }
    }

    private class ConnectionThread extends Thread {

        @Override
        public void run() {
            InetAddress serverAddr;
            try {
                serverAddr = InetAddress.getByAddress(ADDRESS);
                socket = new Socket(serverAddr, SERVER_PORT);

                out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                readThread = new ReadThread(in);
                readThread.start();

            } catch (Exception e) {
                Log.e("", "", e);
            }
        }
    }
    
    private void unwrapMessageAndPassOn(MessageWrapper wrapper){
        if(MessageWrapper.TYPE_RESPONSE.equals(wrapper.getType())){
            callbacks.onResponseReceived(RequestFor.requestForMessage(wrapper.getAction()), wrapper.getPayload());
        }
        else if(MessageWrapper.TYPE_NOTIFICATION.equals(wrapper.getType())){
            SessionMessage msg = SessionMessage.fromJsonString(wrapper.getPayload(), SessionMessage.class);
            callbacks.onNotificationReceived(NotificationFor.requestForMessage(wrapper.getAction()), msg);
        }
    }
    
    public interface NIOConnectionHandlerCallbacks{
        public void onResponseReceived(RequestFor request,String payload);
        public void onNotificationReceived(NotificationFor notification, SessionMessage sessionMessage);
    }
}
