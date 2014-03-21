
package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;
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

    public void handleMessage(String message, ChannelHandlerContext ctx) throws Exception {
        if (!handleError(message, ctx)) {
            peelAndRespond(message, ctx);
        }
    }

    public void onChannelDisconnected(ChannelHandlerContext ctx) {
        for (Entry<Long, SessionConnectionContext> entry : sessionConnections.entrySet()) {
            Player disconnected = entry.getValue().removeConnected(ctx);
            if (disconnected != null) {
                disconnected.setActive(false);
                SessionMessage<Player> sessionMsg = new SessionMessage<Player>(disconnected, entry
                        .getValue().getSession(), disconnected);
                MessageWrapper<SessionMessage<Player>> wrapper = new MessageWrapper<SessionMessage<Player>>(
                        MessageWrapper.TYPE_NOTIFICATION,
                        NotificationFor.UserConnectionState.getAction(), sessionMsg);
                broadcastToSessionParticipants(entry.getValue(), wrapper,
                        Lists.newArrayList(disconnected), false);
            }
        }
    }

    private boolean handleError(String message, ChannelHandlerContext ctx) {
        return false;
    }

    private void peelAndRespond(String msg, ChannelHandlerContext ctx) throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(msg);
        JsonObject object = element.getAsJsonObject();
        String action = object.get(MessageWrapper.FIELD_ACTION).getAsString();
        RequestFor request = RequestFor.requestForMessage(action);
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

    // TODO : simplify request methods and in game methods (wrap around), add
    // error messages in wrappers, add error msg on exception thrown in main
    // server class

    // TODO : create permission check on join to session

    private void finishSession(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<SessionMessage<Object>> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<SessionMessage<Object>>>() {
                }.getType());
        SessionMessage<Object> sessionMsg = msg.getPayload();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        SessionMessage<Session> sessionResponse = new SessionMessage<Session>(
                sessionMsg.getPlayerId(), sessionMsg.getSessionId(), cachedSession);
        MessageWrapper<SessionMessage<Session>> wrapper = new MessageWrapper<SessionMessage<Session>>(
                MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.CloseSession.getAction(), sessionResponse);
        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());

        // alright everybody, time to hit the road
        broadcastToSessionParticipants(sessionCtx, wrapper, null, true);
    }

    private void revealVotes(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<SessionMessage<Long>> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<SessionMessage<Long>>>() {
                }.getType());
        SessionMessage<Long> sessionMsg = msg.getPayload();
        Long ticketId = sessionMsg.getSessionSubject();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());

        Ticket ticket = dao.getTicketDao().queryForId(ticketId);
        // broadcast
        SessionMessage<Ticket> sessionResponse = new SessionMessage<Ticket>(cachedPlayer,
                cachedSession, ticket);
        MessageWrapper<SessionMessage<Ticket>> wrapper = new MessageWrapper<SessionMessage<Ticket>>(
                MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.RevealVotes.getAction(), sessionResponse);

        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        broadcastToSessionParticipants(sessionCtx, wrapper, null, false);
    }

    private void simpleVote(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<SessionMessage<Vote>> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<SessionMessage<Vote>>>() {
                }.getType());
        SessionMessage<Vote> sessionMsg = msg.getPayload();
        Vote newVote = sessionMsg.getSessionSubject();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        Ticket cachedTicket = dao.getTicketDao().queryForId(newVote.getTicketId());
        newVote.setPlayer(cachedPlayer);
        newVote.setTicket(cachedTicket);
        // allowing for changing user's mind
        dao.getVoteDao().createOrUpdate(newVote);

        // broadcast
        SessionMessage<Vote> sessionResponse = new SessionMessage<Vote>(cachedPlayer,
                cachedSession, newVote);
        MessageWrapper<SessionMessage<Vote>> wrapper = new MessageWrapper<SessionMessage<Vote>>(
                MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.UserVote.getAction(), sessionResponse);

        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        broadcastToSessionParticipants(sessionCtx, wrapper, null, false);
    }

    private void newTicketRound(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<SessionMessage<Ticket>> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<SessionMessage<Ticket>>>() {
                }.getType());

        SessionMessage<Ticket> sessionMsg = msg.getPayload();
        Ticket newTicket = sessionMsg.getSessionSubject();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());

        // allowing for re-fetching and updating
        dao.getTicketDao().createOrUpdate(newTicket);
        cachedSession.addTicket(newTicket);

        SessionMessage<Ticket> sessionResponse = new SessionMessage<Ticket>(cachedPlayer,
                cachedSession, newTicket);
        MessageWrapper<SessionMessage<Ticket>> wrapper = new MessageWrapper<SessionMessage<Ticket>>(
                MessageWrapper.TYPE_NOTIFICATION,
                NotificationFor.NextTicket.getAction(), sessionResponse);

        SessionConnectionContext sessionCtx = sessionConnections.get(sessionMsg.getSessionId());
        broadcastToSessionParticipants(sessionCtx, wrapper, null, false);

    }

    @SuppressWarnings("rawtypes")
    private void joinSession(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<SessionMessage> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<SessionMessage>>() {
                }.getType());
        SessionMessage sessionMsg = msg.getPayload();
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        connectPlayerToSession(cachedSession, cachedPlayer, ctx);
        // error message goes here
    }

    private void playerHandshake(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<List<Player>> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<List<Player>>>() {
                }.getType());
        List<Long> playerIds = new ArrayList<Long>();
        List<Player> sentPlayers = msg.getPayload();
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

        MessageWrapper<List<Player>> wrapper = new MessageWrapper<List<Player>>(
                MessageWrapper.TYPE_RESPONSE,
                RequestFor.PlayerHandshake.getMessage(), sentPlayers);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }

    // TODO : create more convenient DAO that will fetch dependencies

    private void playersInLiveSession(ChannelHandlerContext ctx, String stringMsg) {
        // TODO : check if the player has permission to see players in this
        // session
        MessageWrapper<SessionMessage<Object>> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<SessionMessage<Object>>>() {
                }.getType());
        SessionMessage<Object> sessionMessage = msg.getPayload();
        SessionConnectionContext sessionContext = this.sessionConnections.get(sessionMessage
                .getSessionId());
        if (sessionContext != null) {
            List<Player> activePlayers = new ArrayList<Player>(sessionContext.getConnectedPlayers()
                    .keySet());
            MessageWrapper<List<Player>> wrapper = new MessageWrapper<List<Player>>(
                    MessageWrapper.TYPE_RESPONSE,
                    RequestFor.PlayersInLiveSession.getMessage(), activePlayers);
            PokerServerHandler.respond(wrapper.serialize(), ctx, false);
        }
        else {
            // send no such session error message
        }
    }

    private void fetchUserSession(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<Player> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<Player>>() {
                }.getType());
        Player player = msg.getPayload();
        PreparedQuery<Session> querySession = PlayerSession.makeSessionsForExternalUserIdQuery(dao);
        querySession.setArgumentHolderValue(0, player);
        List<Session> playerSessions = dao.getSessionDao().query(querySession);
        for (Session s : playerSessions) {
            PreparedQuery<Player> sessionPlayersQuery = PlayerSession.makePlayerForSession(dao);
            sessionPlayersQuery.setArgumentHolderValue(0, s);
            List<Player> players = dao.getPlayerDao().query(sessionPlayersQuery);
            s.setPlayers(players);
        }

        MessageWrapper<List<Session>> wrapper = new MessageWrapper<List<Session>>(
                MessageWrapper.TYPE_RESPONSE,
                RequestFor.SessionForPlayer.getMessage(), playerSessions);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }

    private void createSession(ChannelHandlerContext ctx, String stringMsg) throws Exception {
        MessageWrapper<Session> msg = AbstractMessage.fromJsonString(stringMsg,
                new TypeToken<MessageWrapper<Session>>() {
                }.getType());
        Session session = msg.getPayload();
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

        MessageWrapper<Session> wrapper = new MessageWrapper<Session>(MessageWrapper.TYPE_RESPONSE,
                RequestFor.CreateSession.getMessage(), session);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }

    private void pushDecksList(ChannelHandlerContext ctx) throws SQLException {
        List<Deck> decks = dao.getDeckDao().queryForAll();
        DeckSetMessage out = new DeckSetMessage();
        out.setDecks(decks);
        MessageWrapper<DeckSetMessage> wrapper = new MessageWrapper<DeckSetMessage>(
                MessageWrapper.TYPE_RESPONSE,
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
            SessionMessage<Player> sessionMsg = new SessionMessage<Player>(player, session, player);
            MessageWrapper<SessionMessage<Player>> wrapper = new MessageWrapper<SessionMessage<Player>>(
                    MessageWrapper.TYPE_NOTIFICATION,
                    NotificationFor.UserConnectionState.getAction(), sessionMsg);

            broadcastToSessionParticipants(sessionCtx, wrapper, null, false);
        }
    }

    @SuppressWarnings("rawtypes")//we're not interested in the type as this is output and GSON knows the type of payload
    private void broadcastToSessionParticipants(SessionConnectionContext session,
            MessageWrapper wrapper, List<Player> omit, boolean closeSession) {
        final List<Player> omitPlayers = new ArrayList<Player>();
        if (omit != null) {
            omitPlayers.addAll(omit);
        }

        for (Entry<Player, ChannelHandlerContext> entry : session.getConnectedPlayers()
                .entrySet()) {
            if (!omitPlayers.contains(entry.getKey())) {
                PokerServerHandler.respond(wrapper.serialize(), entry.getValue(), false);
            }
        }

        if (closeSession) {
            session.disconnectSession();
            sessionConnections.remove(session.getSession().getId());
        }
    }

}
