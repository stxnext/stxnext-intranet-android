package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;

public class GameSessionHandler{

    HashMap<String, ChannelHandlerContext> channels;
    
    public GameSessionHandler(HashMap<String, ChannelHandlerContext> channels) {
        this.channels = channels;
    }

    public void handleMessage(SessionMessage message,ChannelHandlerContext ctx){
        
    }
    
}
