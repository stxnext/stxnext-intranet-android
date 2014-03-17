package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.stxnext.management.server.planningpoker.server.PokerServerHandler;
import com.stxnext.management.server.planningpoker.server.database.managers.DAO;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.IncomingMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
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
    //private RequestHandler requestHandler;
    //private GameSessionHandler gameSessionHandler;
    HashMap<String, ChannelHandlerContext> channels;

    public MessageHandler(HashMap<String, ChannelHandlerContext> channels){
        this.channels = channels;
        this.dao = DAO.getInstance();
        //requestHandler = new RequestHandler(channels);
        //gameSessionHandler = new GameSessionHandler(channels);
    }
    
    public void handleMessage(MessageWrapper wrapper,ChannelHandlerContext ctx) throws Exception{
          if(!handleError(wrapper, ctx)){
              peelAndRespond(wrapper, ctx);
          }
        
        
//        if(message.getRequest()!=null){
//            requestHandler.handleMessage(message.getRequest(), ctx);
//        }
//        //allowing to handle multiple messages at once - device must be prepared to handle those in not necessarily same order
//        if(message.getSessionMessage()!=null){
//            gameSessionHandler.handleMessage(message.getSessionMessage(), ctx);
//        }
    }
    
    private boolean handleError(MessageWrapper wrapper,ChannelHandlerContext ctx){
        return false;
    }
    
    private void peelAndRespond(MessageWrapper wrapper,ChannelHandlerContext ctx) throws Exception{
        RequestFor request = RequestFor.requestForMessage(wrapper.getAction());
        switch(request){
            case CardDecks :
                pushDecksList(ctx);
                break;
            case CreateSession:
                createSession(ctx, wrapper);
            default:
                
                break;
        }
    }
    
    
    private void createSession(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception{
        String json = msg.getPayload();
        Session session = Session.fromJsonString(json, Session.class);
        dao.getSessionDao().createOrUpdate(session);
        
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
