
package com.stxnext.management.android.games.poker;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.common.collect.Lists;
import com.stxnext.management.android.R;
import com.stxnext.management.android.games.poker.SetupActivity.SetupActivityListener;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler.NIOConnectionRequestHandlerCallbacks;
import com.stxnext.management.android.ui.dependencies.SessionListAdapter;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;

public class JoinSessionFragment extends SherlockFragment implements SetupActivityListener,
        NIOConnectionRequestHandlerCallbacks {

    View view;
    ListView sessionList;
    SessionListAdapter adapter;
    
    NIOConnectionHandler nioConnectionHandler;
    GameSetupListener listener;

    public JoinSessionFragment(GameSetupListener listener) {
        super();
        this.listener = listener;
        nioConnectionHandler = NIOConnectionHandler.getInstance();
        nioConnectionHandler.addRequestListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        nioConnectionHandler.removeRequestListener(this);
        super.onDestroy();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nioConnectionHandler.enqueueRequest(RequestFor.PlayerHandshake, Lists.newArrayList(listener.getCurrentUser().convertToPlayer()));
    }

    boolean viewCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_join, container,
                false);
       
        sessionList = (ListView) view.findViewById(R.id.sessionList);
        
        viewCreated = true;
        nioConnectionHandler.start(false);
        setFormEnabled(formsEnabled);
        return view;
    }

    boolean formsEnabled = false;

    @Override
    public void setFormEnabled(boolean enabled) {
        formsEnabled = enabled;
        if (!viewCreated)
            return;
    }

    private void inflateList(List<Session> sessions){
        if(adapter == null){
            adapter = new SessionListAdapter(getActivity(), sessions, GameData.getInstance().getDecks());
            sessionList.setAdapter(adapter);
        }
        else{
            adapter.setList(sessions);
        }
    }
    

    @Override
    public void onDecksReceived(MessageWrapper<DeckSetMessage> msg) {
        GameData.getInstance().setDecks(msg.getPayload().getDecks());
        nioConnectionHandler.enqueueRequest(RequestFor.SessionForPlayer, GameData.getInstance().getCurrentHandshakenPlayer());
        Log.e("request", msg.serialize());
    }

    @Override
    public void onCreateSessionReceived(MessageWrapper<Session> msg) {
        Log.e("request", msg.serialize());
    }

    @Override
    public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg) {
        GameData.getInstance().setUserSessions(msg.getPayload());
        inflateList(msg.getPayload());
        Log.e("request", msg.serialize());
    }

    @Override
    public void onPlayersCreateReceived(MessageWrapper<List<Player>> msg) {
        GameData.getInstance().setCurrentHandshakenPlayer(msg.getPayload().get(0));
        nioConnectionHandler.enqueueRequest(RequestFor.CardDecks, null);
        Log.e("request", msg.serialize());
    }

    @Override
    public void onLivePlayersReceived(MessageWrapper<List<Player>> msg) {
        Log.e("request", msg.serialize());
    }

    @Override
    public void onJoinSessionReceived(MessageWrapper<Player> msg) {
        Log.e("request", msg.serialize());
    }

}
