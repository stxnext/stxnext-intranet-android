package com.stxnext.management.android.games.poker.multiplayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.andengine.extension.multiplayer.protocol.client.SocketServerDiscoveryClient;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.SocketServerDiscoveryServer;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.IDiscoveryData.DefaultDiscoveryData;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;

import android.content.Context;

public class WifiConnectionHandler implements ConnectionHandler {

    private SocketServer<SocketConnectionClientConnector> mSocketServer;
    private ServerConnector<SocketConnection> mServerConnector;

    private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();

    private SocketServerDiscoveryServer<DefaultDiscoveryData> mSocketServerDiscoveryServer;
    private SocketServerDiscoveryClient<DefaultDiscoveryData> mSocketServerDiscoveryClient;
    
    Context appContext;
    
    public WifiConnectionHandler(Context context){
        this.appContext = context.getApplicationContext();
        this.initMessagePool();
    }
    
    
    private void initMessagePool() {
    }
    
    
    
}
