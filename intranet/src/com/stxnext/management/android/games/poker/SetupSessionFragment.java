
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.games.poker.SetupActivity.SetupActivityListener;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler.NIOConnectionRequestHandlerCallbacks;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.ui.dependencies.Popup;
import com.stxnext.management.android.ui.dependencies.Popup.OnPopupItemClickListener;
import com.stxnext.management.android.ui.dependencies.PopupItem;
import com.stxnext.management.android.ui.dependencies.UserListAdapter;
import com.stxnext.management.android.ui.dependencies.UserListAdapter.UserAdapterListener;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;

public class SetupSessionFragment extends SherlockFragment implements SetupActivityListener,
        UserAdapterListener, NIOConnectionRequestHandlerCallbacks {

    private View view;
    EditText sessionNameView;
    TextView teamSelector;
    TextView deckSelector;
    EditText teamErrorView;
    ImageView selectAllCheckbox;
    Cursor usersCursor;
    NIOConnectionHandler nioConnectionHandler;
    Session sessionToCreate;

    private Popup teamsPopup;
    private Popup decksPopup;

    GameSetupListener listener;

    ListView participantsListView;
    UserListAdapter participantsAdapter;
    Team selectedTeam;
    Deck selectedDeck;
    DeckSetMessage deckResponse;

    public SetupSessionFragment(GameSetupListener listener) {
        super();
        this.listener = listener;
        nioConnectionHandler = NIOConnectionHandler.getInstance();
        nioConnectionHandler.addRequestListener(this);
        sessionToCreate = new Session();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_setup_session, menu);
    }

    private boolean validateForm(){
        boolean valid = true;
        sessionNameView.setError(null);
        teamErrorView.setError(null);
        int selectedSize = participantsAdapter.getSelectedIds().size();
        String sessionName = sessionNameView.getText().toString().trim();
        if(Strings.isNullOrEmpty(sessionName)){
            valid = false;
            sessionNameView.setError("Session name required!");
        }
        
        if(selectedSize<=1){
            valid = false;
            teamErrorView.setError("Select at least 2 participants");
            teamErrorView.requestFocus();
        }
        
        return valid;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_start_session) {
            if(!validateForm())
                return true;
            
            getSherlockActivity().setProgressBarIndeterminateVisibility(true);
            sessionToCreate.setName(sessionNameView.getText().toString().trim());
            sessionToCreate.setDeckId(selectedDeck.getId());
            sessionToCreate.setOwner(GameData.getInstance().getCurrentHandshakenPlayer());
            List<IntranetUser> users = participantsAdapter.getSelected();
            List<Player> players = new ArrayList<Player>();
            for(IntranetUser user : users){
                players.add(user.convertToPlayer());
            }
            sessionToCreate.setPlayers(players);
            nioConnectionHandler.enqueueRequest(RequestFor.CreateSession, sessionToCreate);
        }
        return true;
    }
    
    @Override
    public void onDestroy() {
        nioConnectionHandler.removeRequestListener(this);
        super.onDestroy();
    }

    private boolean popupSetup;

    private void setupPopupTeams() {
        if (popupSetup)
            return;
        List<Team> teams = listener.getTeams();
        List<PopupItem> popupItems = new ArrayList<PopupItem>();
        for (Team team : teams) {
            popupItems.add(new PopupItem(team.getName(), team));
        }

        teamsPopup = new Popup(getActivity(), teamSelector);
        teamsPopup.addItems(popupItems);
        selectedTeam = teams.get(0);
        teamsPopup.setSelected(selectedTeam);

        teamsPopup.setOnItemClickListener(new OnPopupItemClickListener() {
            @Override
            public void onItemClick(Object content) {
                selectedTeam = (Team) content;
                teamsPopup.setSelected(content);
                setUpList();
                setupPopupTeams();
            }
        });
        teamSelector.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decksPopup.dismiss();
                teamsPopup.toggle();
            }
        });
        popupSetup = true;
        setUpList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nioConnectionHandler.enqueueRequest(RequestFor.PlayerHandshake, Lists.newArrayList(listener.getCurrentUser().convertToPlayer()));
    }
    
    private void setupDeckPopup(DeckSetMessage msg) {
        List<PopupItem> deckItems = new ArrayList<PopupItem>();

        for (Deck deck : msg.getDecks()) {
            deckItems.add(new PopupItem(deck.getName(), deck));
        }

        decksPopup = new Popup(getActivity(), deckSelector);
        decksPopup.addItems(deckItems);
        selectedDeck = msg.getDecks().get(0);
        decksPopup.setSelected(selectedDeck);

        decksPopup.setOnItemClickListener(new OnPopupItemClickListener() {
            @Override
            public void onItemClick(Object content) {
                selectedDeck = (Deck) content;
                decksPopup.setSelected(content);
            }
        });
        deckSelector.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decksPopup.toggle();
                teamsPopup.dismiss();
            }
        });
    }

    private boolean allSelected;

    private void setAllSelectedState(boolean state) {
        allSelected = state;
        participantsAdapter.setAllSelected(allSelected);
        selectAllCheckbox.setImageLevel(allSelected ? 1 : 0);
    }

    boolean viewCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setup_session, container,
                false);
        sessionNameView = (EditText) view.findViewById(R.id.sessionNameView);
        teamSelector = (TextView) view.findViewById(R.id.teamSelector);
        participantsListView = (ListView) view.findViewById(R.id.usersList);
        selectAllCheckbox = (ImageView) view.findViewById(R.id.selectAllCheckbox);
        deckSelector = (TextView) view.findViewById(R.id.deckSelector);
        teamErrorView = (EditText) view.findViewById(R.id.teamSelectorError);
        selectAllCheckbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelected = !allSelected;
                setAllSelectedState(allSelected);
            }
        });
        viewCreated = true;
        nioConnectionHandler.start(false);
        
        setFormEnabled(formsEnabled);
        return view;
    }
    
    private void setLoading(boolean loading){
        getSherlockActivity().setProgressBarIndeterminateVisibility(loading);
        setFormEnabled(!loading);
    }

    private void setUpList() {
        Cursor c = DAO.getInstance().getIntranetUser()
                .getCursorByTeamId(selectedTeam.getId().longValue());
        int count = c.getCount();
        Log.e("", "result count :" + count);
        if (participantsAdapter == null) {
            usersCursor = c;
            getActivity().startManagingCursor(c);
            participantsAdapter = new UserListAdapter(getActivity(), c, participantsListView);
            participantsAdapter.setListener(this);
            participantsAdapter.setDontUseCursorCache(true);
            participantsListView.setAdapter(participantsAdapter);
            participantsAdapter.setUsesSelection(true);
        }
        else {
            getActivity().stopManagingCursor(usersCursor);
            getActivity().startManagingCursor(c);
            usersCursor = c;
            participantsAdapter.changeCursor(c);
            participantsAdapter.notifyDataSetChanged();
            // participantsListView.invalidateViews();
        }
    }

    boolean formsEnabled = false;

    @Override
    public void setFormEnabled(boolean enabled) {
        formsEnabled = enabled;
        if (!viewCreated)
            return;
        if (enabled) {
            setupPopupTeams();
        }
    }

    @Override
    public void onSelectionEdgeState(boolean allSelected) {
        this.allSelected = allSelected;
        setAllSelectedState(allSelected);
    }

    @Override
    public void onDecksReceived(MessageWrapper<DeckSetMessage> msg) {
        deckResponse = msg.getPayload();
        GameData.getInstance().setDecks(deckResponse.getDecks());
        if (viewCreated) {
            setupDeckPopup(deckResponse);
        }
        Log.e("request", msg.serialize());
    }

    @Override
    public void onCreateSessionReceived(final MessageWrapper<Session> msg) {
        Log.e("request", msg.serialize());
        setFormEnabled(true);
        Builder builder = new android.app.AlertDialog.Builder(getActivity())
        .setTitle("Session created")
        .setMessage("Do you wish to join session named \"" + msg.getPayload().getName() + "\"?")
        .setNegativeButton(getString(R.string.common_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
        .setPositiveButton(getString(R.string.common_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        joinSession(msg.getPayload());
                    }
                });
        builder.show();
    }
    
    private void joinSession(Session session) {
        setLoading(true);
        GameData.getInstance().setSessionIamIn(session);
        nioConnectionHandler.enqueueRequest(RequestFor.JoinSession, new SessionMessage<Object>(
                GameData.getInstance().getCurrentHandshakenPlayer(), session, null));
    }

    @Override
    public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg) {
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

    private boolean joinedSession;
    @Override
    public void onJoinSessionReceived(MessageWrapper<Player> msg) {
        setLoading(false);
        Log.e("request", msg.serialize());
        if (joinedSession)
            return;
        if (msg.getPayload() != null
                && GameData.getInstance().getCurrentHandshakenPlayer().getId()
                        .equals(msg.getPayload().getId())) {
            listener.onSessionJoin();
            joinedSession = true;
        }
    }

}
