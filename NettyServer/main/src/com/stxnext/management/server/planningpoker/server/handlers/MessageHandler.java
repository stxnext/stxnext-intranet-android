package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.HashMap;

import com.stxnext.management.server.planningpoker.server.dto.messaging.in.IncomingMessage;

/**
 * shuold be a singleton that just handles messages and assigning specific type of msg to particular handler
 * will have db and SocketContext set to respond to devices
 * Will have db access and also should have be responsible for separating the game sessions
 * @author luczakp
 *
 */
public class MessageHandler {
    
    private RequestHandler requestHandler;
    private GameSessionHandler gameSessionHandler;
    HashMap<String, ChannelHandlerContext> channels;

    public MessageHandler(HashMap<String, ChannelHandlerContext> channels){
        this.channels = channels;
        requestHandler = new RequestHandler(channels);
        gameSessionHandler = new GameSessionHandler(channels);
    }
    
    public void handleMessage(IncomingMessage message,ChannelHandlerContext ctx) throws SQLException{
        if(message.getRequest()!=null){
            requestHandler.handleMessage(message.getRequest(), ctx);
        }
        //allowing to handle multiple messages at once - device must be prepared to handle those in not necessarily same order
        if(message.getSessionMessage()!=null){
            gameSessionHandler.handleMessage(message.getSessionMessage(), ctx);
        }
    }
}
