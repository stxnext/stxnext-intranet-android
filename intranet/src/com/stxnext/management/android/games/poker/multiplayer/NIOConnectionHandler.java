
package com.stxnext.management.android.games.poker.multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.NotificationFor;

// we know what we are doing here: Only case where types HAS to be declared is
// where declaring typetoken right before deserializing from json to POJO
@SuppressWarnings({"rawtypes","unchecked"})
public class NIOConnectionHandler implements ConnectionHandler {

    static NIOConnectionHandler _instance;

    ConnectionThread connectionThread;
    Handler handler;
    boolean connected;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    ReadThread readThread;
    volatile boolean requestAwaitingForResponse;
    List<NIOConnectionNotificationHandlerCallbacks> notificationCallbacks;
    List<NIOConnectionRequestHandlerCallbacks> requestCallbacks;

    private Queue<MessageWrapper> requestQueue;

    public static NIOConnectionHandler getInstance() {
        if (_instance == null) {
            _instance = new NIOConnectionHandler();
        }
        return _instance;
    }

    public void addNotificationListener(NIOConnectionNotificationHandlerCallbacks listener) {
        notificationCallbacks.add(listener);
    }

    public void addRequestListener(NIOConnectionRequestHandlerCallbacks listener) {
        requestCallbacks.add(listener);
    }
    
    public void removeNotificationListener(NIOConnectionNotificationHandlerCallbacks listener){
        notificationCallbacks.remove(listener);
    }
    public void removeRequestListener(NIOConnectionRequestHandlerCallbacks listener) {
        requestCallbacks.remove(listener);
    }
    

    // TODO : prepare queue guarding thread that checks if socket is not clogged
    // which may happen in simultaneout both direction streaming
    // also server could just don't respond
    public NIOConnectionHandler() {
        handler = new Handler();
        requestQueue = new ArrayBlockingQueue<MessageWrapper>(200);
        requestCallbacks = new ArrayList<NIOConnectionHandler.NIOConnectionRequestHandlerCallbacks>();
        notificationCallbacks = new ArrayList<NIOConnectionHandler.NIOConnectionNotificationHandlerCallbacks>();
    }

    public void publishResonse(String stringMsg) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(stringMsg);
        JsonObject object = element.getAsJsonObject();
        String action = object.get(MessageWrapper.FIELD_ACTION).getAsString();
        String type = object.get(MessageWrapper.FIELD_TYPE).getAsString();
        if (MessageWrapper.TYPE_NOTIFICATION.equals(type)) {
            NotificationFor notification = NotificationFor.notificationForAction(action);
            MessageWrapper msg;
            for (NIOConnectionNotificationHandlerCallbacks clb : notificationCallbacks) {
                if (clb == null)
                    continue;
                switch (notification) {
                    case UserConnectionState:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<SessionMessage<Player>>>() {
                                }.getType());
                        clb.onUserConnectionStateChanged(msg);
                        break;
                    case NextTicket:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<SessionMessage<Ticket>>>() {
                                }.getType());
                        clb.onNewTicketRoundReceived(msg);
                        break;
                    case RevealVotes:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<SessionMessage<Ticket>>>() {
                                }.getType());
                        clb.onRevealVotesReceived(msg);
                        break;
                    case UserVote:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<SessionMessage<Vote>>>() {
                                }.getType());
                        clb.onVoteReceived(msg);
                        break;
                    case CloseSession:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<SessionMessage<Session>>>() {
                                }.getType());
                        clb.onFinishSessionReceived(msg);
                        break;
                }
            }

        }
        else if (MessageWrapper.TYPE_RESPONSE.equals(type)) {
            RequestFor request = RequestFor.requestForMessage(action);
            MessageWrapper msg;
            for (NIOConnectionRequestHandlerCallbacks clb : requestCallbacks) {
                if (clb == null)
                    continue;
                switch (request) {
                    case CardDecks:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<DeckSetMessage>>() {
                                }.getType());
                        clb.onDecksReceived(msg);
                        break;
                    case CreateSession:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<Session>>() {
                                }.getType());
                        clb.onCreateSessionReceived(msg);
                        break;
                    case SessionForPlayer:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<List<Session>>>() {
                                }.getType());
                        clb.onPlayerSessionReceived(msg);
                        break;
                    case PlayerHandshake:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<List<Player>>>() {
                                }.getType());
                        clb.onPlayersCreateReceived(msg);
                        break;
                    case PlayersInLiveSession:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<List<Player>>>() {
                                }.getType());
                        clb.onLivePlayersReceived(msg);
                        break;
                    case JoinSession:
                        msg = AbstractMessage.fromJsonString(stringMsg,
                                new TypeToken<MessageWrapper<Player>>() {
                                }.getType());
                        clb.onJoinSessionReceived(msg);
                        break;
                }
            }
        }
    }

    public void enqueueRequest(RequestFor request, Object message) {
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_REQUEST,
                request.getMessage(), message);
        if (requestAwaitingForResponse || out == null) {
            requestQueue.add(wrapper);
        }
        else {
            dispatchRequest(wrapper);
        }
    }

    private void dispatchRequest(final MessageWrapper wrapper) {
        if (wrapper == null)
            return;

        requestAwaitingForResponse = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String outString = wrapper.serialize();
                out.write(outString + "\r\n");
                out.flush();
            }
        }).start();
    }

    public void start(boolean restartIfOpened) {
        if(restartIfOpened){
            if (connectionThread != null) {
                connectionThread.interrupt();
            }
            connectionThread = new ConnectionThread();
            connectionThread.start();
        }
        else{
            if(connectionThread == null || socket == null){
                connectionThread = new ConnectionThread();
                connectionThread.start();
            }
        }
    }
    
    public void reset(){
        stop();
        start(false);
    }

    public void stop() {
        requestQueue.clear();
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (Exception e) {
                Log.e("", "", e);
            }
            socket = null;
            out = null;
            in = null;
        }
        if (connectionThread != null) {
            connectionThread.interrupt();
            connectionThread = null;
        }
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
    }

    private class ReadThread extends Thread {
        public ReadThread() {
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
                    if (line != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                publishResonse(line);
                            }
                        });
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
            InetSocketAddress serverAddr;
            try {
                serverAddr = new InetSocketAddress(ADDRESS, SERVER_PORT);// InetAddress.getByAddress(ADDRESS);
                socket = new Socket();
                socket.connect(serverAddr, TIMEOUT);

                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                readThread = new ReadThread();
                readThread.start();
                dispatchRequest(requestQueue.poll());

            } catch (Exception e) {
                Log.e("", "", e);
            }
        }
    }

    public interface NIOConnectionRequestHandlerCallbacks {
        public void onDecksReceived(MessageWrapper<DeckSetMessage> msg);

        public void onCreateSessionReceived(MessageWrapper<Session> msg);

        public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg);

        public void onPlayersCreateReceived(MessageWrapper<List<Player>> msg);

        public void onLivePlayersReceived(MessageWrapper<List<Player>> msg);

        public void onJoinSessionReceived(MessageWrapper<Player> msg);
    }

    public interface NIOConnectionNotificationHandlerCallbacks {
        public void onUserConnectionStateChanged(MessageWrapper<SessionMessage<Player>> msg);

        public void onNewTicketRoundReceived(MessageWrapper<SessionMessage<Ticket>> msg);

        public void onVoteReceived(MessageWrapper<SessionMessage<Vote>> msg);

        public void onRevealVotesReceived(MessageWrapper<SessionMessage<Ticket>> msg);

        public void onFinishSessionReceived(MessageWrapper<SessionMessage<Session>> msg);

    }
}
