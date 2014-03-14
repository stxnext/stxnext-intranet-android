package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.stxnext.management.server.planningpoker.server.PokerServerHandler;
import com.stxnext.management.server.planningpoker.server.database.managers.DAO;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;

public class RequestHandler {

    HashMap<String, ChannelHandlerContext> channels;
    DAO dao;
    
    public RequestHandler(HashMap<String, ChannelHandlerContext> channels){
        this.channels = channels;
        this.dao = DAO.getInstance();
    }
    
    public void handleMessage(RequestMessage message,ChannelHandlerContext ctx) throws SQLException{
        if(RequestFor.CardDecks.getMessage().equals(message.getRequestName())){
            pushDecksList(ctx);
        }
        else if(RequestFor.OngoingSession.getMessage().equals(message.getRequestName())){
            
        }
    }
    
    private void pushDecksList(ChannelHandlerContext ctx) throws SQLException{
        List<Deck> decks = dao.getDeckDao().queryForAll();
        DeckSetMessage out = new DeckSetMessage();
        out.setDecks(decks);
        PokerServerHandler.respond(out.serialize(), ctx, true);
    }
    
    private void pushOngoingSessions(){
        
    }
}
