package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.PreparedQuery;
import com.stxnext.management.server.planningpoker.server.PokerServerHandler;
import com.stxnext.management.server.planningpoker.server.database.managers.DAO;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.PlayerSession;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.messaging.GsonProvider;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;

/**
 * shuold be a singleton that just handles messages and assigning specific type of msg to particular handler
 * will have db and SocketContext set to respond to devices
 * Will have db access and also should have be responsible for separating the game sessions
 * @author luczakp
 *
 */
public class MessageHandler {
    
    DAO dao;
    HashMap<String, ChannelHandlerContext> channels;
    HashMap<Long, SessionConnectionContext> sessionConnections;

    public MessageHandler(HashMap<String, ChannelHandlerContext> channels){
        this.channels = channels;
        this.dao = DAO.getInstance();
        this.sessionConnections = new LinkedHashMap<Long, SessionConnectionContext>();
    }
    
    public void handleMessage(MessageWrapper wrapper,ChannelHandlerContext ctx) throws Exception{
          if(!handleError(wrapper, ctx)){
              peelAndRespond(wrapper, ctx);
          }
    }
    
    private boolean handleError(MessageWrapper wrapper,ChannelHandlerContext ctx){
        return false;
    }
    
    private void peelAndRespond(MessageWrapper msg,ChannelHandlerContext ctx) throws Exception{
        RequestFor request = RequestFor.requestForMessage(msg.getAction());
        switch(request){
            case CardDecks :
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
            default:
                
                break;
        }
    }

    
    
    private void joinSession(ChannelHandlerContext ctx,MessageWrapper msg) throws Exception{
        String json = msg.getPayload();
        SessionMessage sessionMsg = SessionMessage.fromJsonString(json, SessionMessage.class);
        Session cachedSession = dao.getSessionDao().queryForId(sessionMsg.getSessionId());
        Player cachedPlayer = dao.getPlayerDao().queryForId(sessionMsg.getPlayerId());
        if(cachedSession!=null && cachedPlayer != null){
            SessionConnectionContext sessionCtx = new SessionConnectionContext();
            sessionCtx.setSession(cachedSession);
            sessionCtx.addConnectedPlayer(cachedPlayer, ctx);
        }
        // error message goes here
    }
    
    
    private void playerHandshake(ChannelHandlerContext ctx,MessageWrapper msg) throws Exception{
        String json = msg.getPayload();
        
        List<Long> playerIds = new ArrayList<Long>();
        List<Player> sentPlayers = Player.fromJsonString(json, new TypeToken<ArrayList<Player>>(){}.getType());
        for(Player player : sentPlayers){
            playerIds.add(player.getExternalId());
        }
        
        PreparedQuery<Player> query = dao.getPlayerDao().queryBuilder().where().in(Player.FIELD_EXTERNAL_ID, playerIds).prepare();
        List<Player> foundPlayers = dao.getPlayerDao().query(query);
        
        for(Player player : sentPlayers){
            if(foundPlayers.contains(player)){
                player.setId(foundPlayers.get(foundPlayers.indexOf(player)).getId());
            }
            else{
                dao.getPlayerDao().createOrUpdate(player);
            }
        }
        
        Gson gson = GsonProvider.get();
        String output = gson.toJson(sentPlayers, new TypeToken<ArrayList<Player>>(){}.getType());
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE, RequestFor.PlayerHandshake.getMessage(), output);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }
    
    private void fetchUserSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        String json = msg.getPayload();
        Player player = Player.fromJsonString(json, Player.class);
        PreparedQuery<Session> querySession = PlayerSession.makeSessionsForExternalUserIdQuery(dao);
        querySession.setArgumentHolderValue(0, player);
        
        List<Session> playerSessions = dao.getSessionDao().query(querySession);
        Gson gson = GsonProvider.get();
        String output = gson.toJson(playerSessions, new TypeToken<ArrayList<Session>>(){}.getType());
        
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE, RequestFor.SessionForPlayer.getMessage(), output);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }
    
    private void createSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        String json = msg.getPayload();
        Session session = Session.fromJsonString(json, Session.class);
        dao.getSessionDao().createOrUpdate(session);
        dao.getPlayerDao().createOrUpdate(session.getOwner());
        PlayerSession playerSession = new PlayerSession(session.getOwner(), session);
        dao.getPlayerSessionDao().createOrUpdate(playerSession);
        
        for(Player player : session.getPlayers()){
            dao.getPlayerDao().createOrUpdate(player);
            playerSession = new PlayerSession(player, session);
            dao.getPlayerSessionDao().createOrUpdate(playerSession);
        }
        
        String output = session.serialize();
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE, RequestFor.CreateSession.getMessage(), output);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }
    
    private void pushDecksList(ChannelHandlerContext ctx) throws SQLException{
        List<Deck> decks = dao.getDeckDao().queryForAll();
        DeckSetMessage out = new DeckSetMessage();
        out.setDecks(decks);
        String output = out.serialize();
        MessageWrapper wrapper = new MessageWrapper(MessageWrapper.TYPE_RESPONSE, RequestFor.CardDecks.getMessage(), output);
        PokerServerHandler.respond(wrapper.serialize(), ctx, false);
    }
    
}
