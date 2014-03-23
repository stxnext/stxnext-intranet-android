
package com.stxnext.management.android.games.poker.multiplayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import android.os.Handler;
import android.util.Log;

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

public class NIOConnectionHandler implements ConnectionHandler {

    private static final int RESPONSE_NOTIFICATION = 1;
    private static final int RESPONSE_REQUEST = 2;

    static NIOConnectionHandler _instance;

    ConnectionThread connectionThread;
    Handler handler;
    boolean connected;
    Socket socket;
    PrintWriter out;
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

    // TODO : prepare queue guarding thread that checks if socket is not clogged
    // which may happen in simultaneout both direction streaming
    // also server could just don't respond
    public NIOConnectionHandler() {
        handler = new Handler();
        requestQueue = new ArrayBlockingQueue<MessageWrapper>(200);
        requestCallbacks = new ArrayList<NIOConnectionHandler.NIOConnectionRequestHandlerCallbacks>();
        notificationCallbacks = new ArrayList<NIOConnectionHandler.NIOConnectionNotificationHandlerCallbacks>();
    }

    public void publishResonse(Object response, MessageWrapper msg) {
        if (response instanceof NotificationFor) {
            NotificationFor notification = (NotificationFor) response;
            for (NIOConnectionNotificationHandlerCallbacks clb : notificationCallbacks) {
                if (clb == null)
                    continue;

                switch (notification) {
                    case UserConnectionState:
                        clb.onJoinSessionReceived(msg);
                        break;
                    case NextTicket:
                        clb.onNewTicketRoundReceived(msg);
                        break;
                    case RevealVotes:
                        clb.onRevealVotesReceived(msg);
                        break;
                    case UserVote:
                        clb.onVoteReceived(msg);
                        break;
                    case CloseSession:
                        clb.onFinishSessionReceived(msg);
                        break;
                }
            }
        }
        else if (response instanceof RequestFor) {
            RequestFor request = (RequestFor) response;
            for (NIOConnectionRequestHandlerCallbacks clb : requestCallbacks) {
                if (clb == null)
                    continue;
                switch (request) {
                    case CardDecks:
                        clb.onDecksReceived(msg);
                        break;
                    case CreateSession:
                        clb.onCreateSessionReceived(msg);
                        break;
                    case SessionForPlayer:
                        clb.onPlayerSessionReceived(msg);
                        break;
                    case PlayerHandshake:
                        clb.onPlayersCreateReceived(msg);
                        break;
                    case PlayersInLiveSession:
                        clb.onLivePlayersReceived(msg);
                        break;
                    case JoinSession:
                        // TODO : player requesting join should also receive a
                        // response and be omitted in notified group
                        break;
                }
            }
        }
    }

    public void publishOnNotificationReceived(NotificationFor notification,
            SessionMessage sessionMessage) {

    }

    public void enqueueRequest(RequestFor request, AbstractMessage message) {
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_REQUEST,
                request.getMessage(), message.serialize());
        if (requestAwaitingForResponse) {
            requestQueue.add(wrapper);
        }
        else {
            dispatchRequest(wrapper);
        }
    }

    private void dispatchRequest(MessageWrapper wrapper) {
        if (wrapper == null)
            return;

        requestAwaitingForResponse = true;
        String outString = wrapper.serialize();
        out.write(outString + "\r\n");
        out.flush();
    }

    public void start() {
        if (connectionThread != null) {
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
                    if (line != null) {
                        unwrapMessageAndPassOn(line);
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

    private void unwrapMessageAndPassOn(String stringMessage) {
        
        
        
    }

    public interface NIOConnectionRequestHandlerCallbacks {
        public void onDecksReceived(MessageWrapper<DeckSetMessage> msg);

        public void onCreateSessionReceived(MessageWrapper<Session> msg);

        public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg);

        public void onPlayersCreateReceived(MessageWrapper<List<Player>> msg);

        public void onLivePlayersReceived(MessageWrapper<List<Player>> msg);
    }

    public interface NIOConnectionNotificationHandlerCallbacks {
        public void onJoinSessionReceived(MessageWrapper<SessionMessage<Player>> msg);

        public void onNewTicketRoundReceived(MessageWrapper<SessionMessage<Ticket>> msg);

        public void onVoteReceived(MessageWrapper<SessionMessage<Vote>> msg);

        public void onRevealVotesReceived(MessageWrapper<SessionMessage<Ticket>> msg);

        public void onFinishSessionReceived(MessageWrapper<SessionMessage<Session>> msg);

    }
}
