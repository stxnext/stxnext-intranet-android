
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.stxnext.management.android.R;
import com.stxnext.management.android.games.poker.SetupActivity.SetupActivityListener;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler.NIOConnectionNotificationHandlerCallbacks;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler.NIOConnectionRequestHandlerCallbacks;
import com.stxnext.management.android.ui.dependencies.PlayerListAdapter;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;

public class SessionPreviewFragment extends SherlockFragment implements SetupActivityListener,
        NIOConnectionNotificationHandlerCallbacks, NIOConnectionRequestHandlerCallbacks {

    View view;
    ListView playersList;
    TextView sessionInfo;
    PlayerListAdapter adapter;

    NIOConnectionHandler nioConnectionHandler;
    GameSetupListener listener;
    List<Player> livePlayers;
    
    private final static Comparator<Player> listComparator = new Comparator<Player>() {
        @Override
        public int compare(Player lhs, Player rhs) {
            return String.CASE_INSENSITIVE_ORDER.compare(lhs.getName(), rhs.getName());
        }
    };

    public SessionPreviewFragment(GameSetupListener listener) {
        super();
        this.listener = listener;
        livePlayers = new ArrayList<Player>();
        nioConnectionHandler = NIOConnectionHandler.getInstance();
        nioConnectionHandler.addNotificationListener(this);
        nioConnectionHandler.addRequestListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onDestroy() {
        nioConnectionHandler.removeNotificationListener(this);
        nioConnectionHandler.removeRequestListener(this);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        nioConnectionHandler.enqueueRequest(RequestFor.PlayersInLiveSession,
                new SessionMessage<Object>(GameData.getInstance().getCurrentHandshakenPlayer(),GameData.getInstance().getSessionIamIn(),null));
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_session_preview, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.go_into_session) {
            startActivity(new Intent(getActivity(), BoardGameActivity.class));
        }
        getActivity().finish();
        return true;
    }

    boolean viewCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_preview, container,
                false);

        playersList = (ListView) view.findViewById(R.id.livePlayersList);
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

    private void inflateList(List<Player> players) {
        if (adapter == null) {
            adapter = new PlayerListAdapter(getActivity(), players, playersList);
            playersList.setAdapter(adapter);
        }
        else {
            Collections.sort(players, listComparator);
            adapter.setList(players);
        }
    }

    @Override
    public void onUserConnectionStateChanged(MessageWrapper<SessionMessage<Player>> msg) {
        Player player = msg.getPayload().getSessionSubject();
        if(player.isActive()){
            if(!livePlayers.contains(player))
                livePlayers.add(player);
        }
        else{
            livePlayers.remove(player);
        }
        inflateList(livePlayers);
    }

    @Override
    public void onNewTicketRoundReceived(MessageWrapper<SessionMessage<Ticket>> msg) {
    }

    @Override
    public void onVoteReceived(MessageWrapper<SessionMessage<Vote>> msg) {
    }

    @Override
    public void onRevealVotesReceived(MessageWrapper<SessionMessage<Ticket>> msg) {
    }

    @Override
    public void onFinishSessionReceived(MessageWrapper<SessionMessage<Session>> msg) {
    }
//REQUESTS
    @Override
    public void onDecksReceived(MessageWrapper<DeckSetMessage> msg) {
    }

    @Override
    public void onCreateSessionReceived(MessageWrapper<Session> msg) {
    }

    @Override
    public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg) {
    }

    @Override
    public void onPlayersCreateReceived(MessageWrapper<List<Player>> msg) {
    }

    @Override
    public void onLivePlayersReceived(MessageWrapper<List<Player>> msg) {
        livePlayers = msg.getPayload();
        inflateList(livePlayers);
    }

    @Override
    public void onJoinSessionReceived(MessageWrapper<Player> msg) {
    }

}
