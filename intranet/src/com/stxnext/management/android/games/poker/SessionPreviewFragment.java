
package com.stxnext.management.android.games.poker;

import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class SessionPreviewFragment extends SherlockFragment implements SetupActivityListener,
        NIOConnectionRequestHandlerCallbacks {

    View view;
    ListView playersList;
    SessionListAdapter adapter;

    NIOConnectionHandler nioConnectionHandler;
    GameSetupListener listener;

    public SessionPreviewFragment(GameSetupListener listener) {
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
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        nioConnectionHandler.enqueueRequest(RequestFor.PlayersInLiveSession,
                Lists.newArrayList(GameData.getInstance().getCurrentHandshakenPlayer()));
    }

    boolean viewCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_preview, container,
                false);

//        sessionList = (ListView) view.findViewById(R.id.sessionList);
//        sessionList.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Session session = (Session) adapter.getItem(position);
//                onSessionSelected(session);
//            }
//        });
        viewCreated = true;
        nioConnectionHandler.start(false);
        setFormEnabled(formsEnabled);
        return view;
    }

    private void onSessionSelected(final Session session) {
        Builder builder = new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Join session?")
                .setMessage("Do you wish to join session named \"" + session.getName() + "\"?")
                .setNegativeButton(getString(R.string.common_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(getString(R.string.common_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                joinSession(session);
                            }
                        });
        builder.show();
    }

    private void joinSession(Session session) {
        nioConnectionHandler.enqueueRequest(RequestFor.JoinSession, session);
    }

    boolean formsEnabled = false;

    @Override
    public void setFormEnabled(boolean enabled) {
        formsEnabled = enabled;
        if (!viewCreated)
            return;
    }

    private void inflateList(List<Session> sessions) {
        if (adapter == null) {
            adapter = new SessionListAdapter(getActivity(), sessions, GameData.getInstance()
                    .getDecks());
           // sessionList.setAdapter(adapter);
        }
        else {
            adapter.setList(sessions);
        }
    }

    @Override
    public void onDecksReceived(MessageWrapper<DeckSetMessage> msg) {
        GameData.getInstance().setDecks(msg.getPayload().getDecks());
        nioConnectionHandler.enqueueRequest(RequestFor.SessionForPlayer, GameData.getInstance()
                .getCurrentHandshakenPlayer());
    }

    @Override
    public void onCreateSessionReceived(MessageWrapper<Session> msg) {
    }

    @Override
    public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg) {
        GameData.getInstance().setUserSessions(msg.getPayload());
        inflateList(msg.getPayload());
    }

    @Override
    public void onPlayersCreateReceived(MessageWrapper<List<Player>> msg) {
        GameData.getInstance().setCurrentHandshakenPlayer(msg.getPayload().get(0));
        nioConnectionHandler.enqueueRequest(RequestFor.CardDecks, null);
    }

    @Override
    public void onLivePlayersReceived(MessageWrapper<List<Player>> msg) {
    }

    @Override
    public void onJoinSessionReceived(MessageWrapper<Player> msg) {
        if (msg.getPayload() != null
                && GameData.getInstance().getCurrentHandshakenPlayer().getId()
                        .equals(msg.getPayload().getId())) {
            listener.getViewPager().setCurrentItem(2, true);
        }
    }

}
