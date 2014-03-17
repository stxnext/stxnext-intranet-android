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
        this.connectedPlayers.put(player, ctx);
    }
    
    public void removeConnected(Player player){
        this.connectedPlayers.remove(player);
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
            this.connectedPlayers.remove(toRemove);
        }
    }
    
}
