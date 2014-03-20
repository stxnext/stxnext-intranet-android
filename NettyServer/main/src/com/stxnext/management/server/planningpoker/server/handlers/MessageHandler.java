
package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.PreparedQuery;
import com.stxnext.management.server.planningpoker.server.PokerServerHandler;
import com.stxnext.management.server.planningpoker.server.database.managers.DAO;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.PlayerSession;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;
import com.stxnext.management.server.planningpoker.server.dto.messaging.GsonProvider;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.NotificationFor;

/**
 * @author luczakp
 */
public class MessageHandler {

    DAO dao;
    HashMap<String, ChannelHandlerContext> channels;
    HashMap<Long, SessionConnectionContext> sessionConnections;

    public MessageHandler(HashMap<String, ChannelHandlerContext> channels) {
        this.channels = channels;
        this.dao = DAO.getInstance();
        this.sessionConnections = new LinkedHashMap<Long, SessionConnectionContext>();
    }

    public void handleMessage(MessageWrapper wrapper, ChannelHandlerContext ctx) throws Exception {
        if (!handleError(wrapper, ctx)) {
            peelAndRespond(wrapper, ctx);
        }
    }
    
    public void onChannelDisconnected(ChannelHandlerContext ctx){
        for(Entry<Long, SessionConnectionContext> entry : sessionConnections.entrySet()){
            Player disconnected = entry.getValue().removeConnected(ctx);
            if(disconnected != null){
                disconnected.setActive(false);
                SessionMessage sessionMsg = new SessionMessage(disconnected, entry.getValue().getSession(), disconnected);
                MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_NOTIFICATION,
                        NotificationFor.UserConnectionState.getAction(), sessionMsg);
                broadcastToSessionParticipants(entry.getValue(), wrapper, Lists.newArrayList(disconnected),false);
            }
        }
    }

    private boolean handleError(MessageWrapper wrapper, ChannelHandlerContext ctx) {
        return false;
    }

    private void peelAndRespond(MessageWrapper msg, ChannelHandlerContext ctx) throws Exception {
        RequestFor request = RequestFor.requestForMessage(msg.getAction());
        switch (request) {
            case CardDecks:
                pushDecksList(ctx);
                break;
            case CreateSession:
                createSession(ctx, msg);
                break;
            case SessionForPlayer:
                fetchUserSession(ctx, msg);
                break;
            case PlayerHandshake:
                playerHandshake(ctx, msg);
                break;
            case JoinSession:
                joinSession(ctx, msg);
                break;
            case PlayersInLiveSession:
                playersInLiveSession(ctx, msg);
                break;
            case SMNewTicketRound:
                newTicketRound(ctx, msg);
                break;
            case SMSimpleVote:
                simpleVote(ctx, msg);
                break;
            case SMRevealVotes:   
                revealVotes(ctx, msg);
                break;
            case SMFinishSession:
                finishSession(ctx, msg);
                break;
            default:
                // some error message - inappropriate request
                break;
        }
    }
    
    private <T> SessionMessage unwrapSessionSubject(Object payload, Class<T> clazz){
        String json = GsonProvider.get().toJson(payload);
        SessionMessage sessionMsg = SessionMessage.fromJsonString(json, SessionMessage.class);
        T subject = GsonProvider.get().fromJson(json, clazz);
        sessionMsg.setSessionSubject(subject);
        return sessionMsg;
    }
    
    // TODO : simplify request methods and in game methods (wrap around), add error messages in wrappers, add error msg on exception thrown in main server class

    // TODO : create permission check on join to session
    
    private void finishSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        //String json = msg.getPayload();
        String json = GsonProvider.get().toJson(msg.getPayload());
        SessionMessage sessionMsg = SessionMessage.fromJsonString(json, SessionMessage.class);
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        SessionMessage sessionResponse = new SessionMessage(sessionMsg.getPlayerId(), sessionMsg.getSessionId(), cachedSession);
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.CloseSession.getAction(), sessionResponse);
        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        
        //alright everybody, time to hit the road
        broadcastToSessionParticipants(sessionCtx, wrapper, null, true);
    }
    
    private void revealVotes(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        SessionMessage sessionMsg = unwrapSessionSubject(msg.getPayload(), Long.class);
        Long ticketId = (Long) sessionMsg.getSessionSubject();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        
        Ticket ticket = dao.getTicketDao().queryForId(ticketId);
        //broadcast
        SessionMessage sessionResponse = new SessionMessage(cachedPlayer, cachedSession, ticket);
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.RevealVotes.getAction(), sessionResponse);
        
        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        broadcastToSessionParticipants(sessionCtx, wrapper, null,false);
    }
    
    private void simpleVote(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        SessionMessage sessionMsg = unwrapSessionSubject(msg.getPayload(), Vote.class);
        Vote newVote = (Vote) sessionMsg.getSessionSubject();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        Ticket cachedTicket = dao.getTicketDao().queryForId(newVote.getTicketId());
        newVote.setPlayer(cachedPlayer);
        newVote.setTicket(cachedTicket);
        //allowing for changing user's mind
        dao.getVoteDao().createOrUpdate(newVote);
        
        //broadcast
        SessionMessage sessionResponse = new SessionMessage(cachedPlayer, cachedSession, newVote);
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.UserVote.getAction(), sessionResponse);
        
        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        broadcastToSessionParticipants(sessionCtx, wrapper, null,false);
    }
    
    private void newTicketRound(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        SessionMessage sessionMsg = unwrapSessionSubject(msg.getPayload(), Ticket.class);
        Ticket newTicket = (Ticket) sessionMsg.getSessionSubject();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        
        //allowing for re-fetching and updating
        dao.getTicketDao().createOrUpdate(newTicket);
        cachedSession.addTicket(newTicket);
        
        SessionMessage sessionResponse = new SessionMessage(cachedPlayer, cachedSession, newTicket);
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.NextTicket.getAction(), sessionResponse);
        
        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        broadcastToSessionParticipants(sessionCtx, wrapper, null,false);
        
        //cachedSession.set
    }
    
    private void joinSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {
        SessionMessage sessionMsg = SessionMessage.fromJsonString(msg.getPayload().toString(), SessionMessage.class);
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        connectPlayerToSession(cachedSession, cachedPlayer, ctx);
        // error message goes here
    }

    private void playerHandshake(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {

        List<Long> playerIds = new ArrayList<Long>();
        List<Player> sentPlayers = Player.fromJsonString(msg.getPayload().toString(), new TypeToken<ArrayList<Player>>() {}.getType());
        for (Player player : sentPlayers) {
            playerIds.add(player.getExternalId());
        }

        PreparedQuery<Player> query = dao.getPlayerDao().queryBuilder().where()
                .in(Player.FIELD_EXTERNAL_ID, playerIds).prepare();
        List<Player> foundPlayers = dao.getPlayerDao().query(query);

        for (Player player : sentPlayers) {
            if (foundPlayers.contains(player)) {
                player.setId(foundPlayers.get(foundPlayers.indexOf(player)).getId());
            }
            else {
                dao.getPlayerDao().createOrUpdate(player);
            }
        }

        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE,
                RequestFor.PlayerHandshake.getMessage(), sentPlayers);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }
    
    // TODO : create more convenient DAO that will fetch dependencies
    
    private void playersInLiveSession(ChannelHandlerContext ctx, MessageWrapper msg){
        // TODO : check if the player has permission to see players in this session
        SessionMessage sessionMessage = SessionMessage.fromJsonString(msg.getPayload().toString(), SessionMessage.class);
        SessionConnectionContext sessionContext = this.sessionConnections.get(sessionMessage.getSessionId());
        if(sessionContext != null){
            List<Player> activePlayers = new ArrayList<Player>(sessionContext.getConnectedPlayers().keySet());
            MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE,
                    RequestFor.PlayersInLiveSession.getMessage(), activePlayers);
            PokerServerHandler.respond(wrapper.serialize(), ctx, false);
        }
        else{
            // send no such session error message
        }
    }

    private void fetchUserSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {
        Player player = Player.fromJsonString(msg.getPayload().toString(), Player.class);
        PreparedQuery<Session> querySession = PlayerSession.makeSessionsForExternalUserIdQuery(dao);
        querySession.setArgumentHolderValue(0, player);
        List<Session> playerSessions = dao.getSessionDao().query(querySession);
        for (Session s : playerSessions) {
            PreparedQuery<Player> sessionPlayersQuery = PlayerSession.makePlayerForSession(dao);
            sessionPlayersQuery.setArgumentHolderValue(0, s);
            List<Player> players = dao.getPlayerDao().query(sessionPlayersQuery);
            s.setPlayers(players);
        }

        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE,
                RequestFor.SessionForPlayer.getMessage(), playerSessions);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }

    private void createSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {
        //String json = msg.getPayload();
        Session session = Session.fromJsonString(msg.getPayload().toString(), Session.class);
        // creating new session as the command says
        dao.getSessionDao().create(session);
        // updating users
        dao.getPlayerDao().createOrUpdate(session.getOwner());
        PlayerSession playerSession = new PlayerSession(session.getOwner(), session);
        dao.getPlayerSessionDao().createOrUpdate(playerSession);

        for (Player player : session.getPlayers()) {
            dao.getPlayerDao().createOrUpdate(player);
            playerSession = new PlayerSession(player, session);
            dao.getPlayerSessionDao().createOrUpdate(playerSession);
        }

        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE,
                RequestFor.CreateSession.getMessage(), session);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }

    private void pushDecksList(ChannelHandlerContext ctx) throws SQLException {
        List<Deck> decks = dao.getDeckDao().queryForAll();
        DeckSetMessage out = new DeckSetMessage();
        out.setDecks(decks);
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE,
                RequestFor.CardDecks.getMessage(), out);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }

    private void connectPlayerToSession(Session session, Player player, ChannelHandlerContext ctx)
            throws Exception {
        if (session != null && player != null) {
            SessionConnectionContext sessionCtx = this.sessionConnections.get(session.getId());
            if (sessionCtx == null) {
                sessionCtx = new SessionConnectionContext();
                sessionCtx.setSession(session);
                sessionConnections.put(session.getId(), sessionCtx);
            }
            sessionCtx.addConnectedPlayer(player, ctx);
            // notify about that

            player.setActive(true);
            dao.getPlayerDao().createOrUpdate(player);
            SessionMessage sessionMsg = new SessionMessage(player, session, player);
            MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_NOTIFICATION,
                    NotificationFor.UserConnectionState.getAction(), sessionMsg);
            
            broadcastToSessionParticipants(sessionCtx, wrapper, null, false);
        }
    }
    
    
    private void broadcastToSessionParticipants(SessionConnectionContext session, MessageWrapper wrapper, List<Player> omit, boolean closeSession){
        final List<Player> omitPlayers = new ArrayList<Player>();
        if(omit!=null){
            omitPlayers.addAll(omit);
        }
        
        for (Entry<Player, ChannelHandlerContext> entry : session.getConnectedPlayers()
                .entrySet()) {
            if (!omitPlayers.contains(entry.getKey())) {
                PokerServerHandler.respond(wrapper.serialize(), entry.getValue(), false);
            }
        }
        
        if(closeSession){
            session.disconnectSession();
            sessionConnections.remove(session.getSession().getId());
        }
    }

}
