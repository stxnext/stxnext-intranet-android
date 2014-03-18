/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.stxnext.management.server.planningpoker.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.IncomingMessage;
import com.stxnext.management.server.planningpoker.server.handlers.MessageHandler;

/**
 * Handles a server-side channel.
 */
@Sharable
public class PokerServerHandler extends SimpleChannelInboundHandler<String> {

    private MessageHandler msgHandler;
    private Logger logger;
    private final HashMap<String, ChannelHandlerContext> channels = new LinkedHashMap<String, ChannelHandlerContext>(200, 0.5f);
    
    public PokerServerHandler(){
        logger = ServerConfigurator.getInstance().getLogger();
        msgHandler = new MessageHandler(channels);
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.put(ctx.channel().remoteAddress().toString(),ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        String msg = ctx.channel().remoteAddress().toString() + "UNREGISTERED \r\n";
        logger.log(Level.WARN, msg);
        broadcastToGroup(ctx.channel().remoteAddress().toString()+" has disconnected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String msg = ctx.channel().remoteAddress().toString() + " INACTIVE \r\n";
        logger.log(Level.WARN, msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        super.channelRead(arg0, arg1);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.log(Level.DEBUG, "request from " + ctx.channel().remoteAddress().toString() + ":"
                + request + "\r\n");
        try{
            request = request.trim();
            MessageWrapper message = MessageWrapper.fromJsonString(request, MessageWrapper.class);
            msgHandler.handleMessage(message, ctx);
        }
        catch(Exception jse){
            logger.log(Level.WARN, jse);
        }
    }
    
    void broadcastToGroup(String message){
        removeInactiveChannels();
        for (ChannelHandlerContext channel : channels.values()) {
            ChannelFuture future = channel.writeAndFlush(message);
        }
        logger.log(Level.INFO,message);
    }
    
    public static void respond(String message,ChannelHandlerContext channel, boolean closeAfter){
        ChannelFuture future = channel.writeAndFlush(message+"\r\n");
        if(closeAfter){
            future.channel().close();
        }
    }
    
    private void removeInactiveChannels(){
        List<String> inactiveChannels = new ArrayList<String>();
        for (Entry<String, ChannelHandlerContext> entry : channels.entrySet()) {
            if(!entry.getValue().channel().isActive() || !entry.getValue().channel().isRegistered()){
                inactiveChannels.add(entry.getKey());
                entry.getValue().close();
            }
        }
        for(String key : inactiveChannels){
            channels.remove(key);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
         logger.log(
         Level.WARN,
         "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
