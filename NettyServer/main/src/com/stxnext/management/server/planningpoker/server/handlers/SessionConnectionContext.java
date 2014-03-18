package com.stxnext.management.server.planningpoker.server.handlers;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;

public class SessionConnectionContext {

    private Session session;
    private HashMap<Player, ChannelHandlerContext> connectedPlayers = new LinkedHashMap<Player, ChannelHandlerContext>();
    
    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }
    public HashMap<Player, ChannelHandlerContext> getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public void addConnectedPlayer(Player player, ChannelHandlerContext ctx){
        ChannelHandlerContext playerContext = this.connectedPlayers.get(player);
        if(playerContext != null){
            //discard old connection - device/player should know what he is doing. If he wants to join again he'll loose his old channel
            //we're not broadcasting that player has been disconnected - he just changed a channel
            playerContext.close();
        }
        this.connectedPlayers.put(player, ctx);
    }
    
    private void removePlayerAndConnection(Player player){
        ChannelHandlerContext playerContext = this.connectedPlayers.get(player);
        if(playerContext != null){
            playerContext.close();
        }
        this.connectedPlayers.remove(player);
    }
    
    public void removeConnected(Player player){
        removePlayerAndConnection(player);
    }
    
    public void removeConnected(ChannelHandlerContext ctx){
        Player toRemove = null;
        for(Entry<Player, ChannelHandlerContext> entry : this.connectedPlayers.entrySet()){
            if(entry.getValue().equals(ctx)){
                toRemove = entry.getKey();
                break;
            }
        }
        
        if(toRemove!=null){
            removePlayerAndConnection(toRemove);
        }
    }
    
}
