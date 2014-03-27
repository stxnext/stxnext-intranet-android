
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler;
import com.stxnext.management.android.ui.dependencies.SimplePlayersGridAdapter;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;

public class GameDashboardFragment extends Fragment {

    private View rootView;
    private NIOConnectionHandler nioHandler;
    private GameData gameData;
    private GameDashboardListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        nioHandler = NIOConnectionHandler.getInstance();
        gameData = GameData.getInstance();
    }
    
    public GameDashboardFragment(GameDashboardListener listener){
        super();
        this.listener = listener;
    }

    boolean viewCreated;
    private View submitTicketArea;
    private EditText ticketNameInput;
    private Button pushTicketButton;
    private View revealArea;
    private Button revealVotesButton;
    private View masterPanel;
    private View participantPanel;
    private Button voteButton;
    private TextView gameStatusInfo;
    private GridView playersGrid;
    private SimplePlayersGridAdapter playerGridAdapter;

    
    
    public SimplePlayersGridAdapter getPlayerGridAdapter() {
        return playerGridAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.game_fragment_dashboard, container,
                false);

        masterPanel = rootView.findViewById(R.id.masterPanel);

        participantPanel = rootView.findViewById(R.id.participantPanel);
        voteButton = (Button) rootView.findViewById(R.id.voteButton);
        submitTicketArea = rootView.findViewById(R.id.submitTicketArea);
        ticketNameInput = (EditText) rootView.findViewById(R.id.ticketNameInput);
        pushTicketButton = (Button) rootView.findViewById(R.id.pushTicketButton);
        revealArea = rootView.findViewById(R.id.revealArea);
        revealVotesButton = (Button) rootView.findViewById(R.id.revealVotesButton);

        gameStatusInfo = (TextView) rootView.findViewById(R.id.gameStatusInfo);
        playersGrid = (GridView) rootView.findViewById(R.id.playersGrid);

        playerGridAdapter = new SimplePlayersGridAdapter(getActivity(), new ArrayList<Player>(), playersGrid);
        playersGrid.setAdapter(playerGridAdapter);
        
        pushTicketButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketNameInput.setError(null);
                String ticketName = ticketNameInput.getText().toString().trim();
                if (!Strings.isNullOrEmpty(ticketName)) {
                    Ticket ticket = new Ticket();
                    ticket.setDisplayValue(ticketName);
                    ticket.setSessionId(gameData.sessionIamIn.getId());
                    nioHandler.enqueueRequest(RequestFor.SMNewTicketRound,
                            gameData.getSessionMessageInstance(ticket));
                    listener.setSubjectText("Adding ticket...");
                }
                else {
                    ticketNameInput.setError("Set ticket name!");
                }
            }
        });
        
        revealVotesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Builder builder = new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Reveal")
                        .setMessage("Are you sure you want to reveal votes now?")
                        .setNegativeButton(getString(R.string.common_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                        .setPositiveButton(getString(R.string.common_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        nioHandler.enqueueRequest(
                                                RequestFor.SMRevealVotes,
                                                gameData.getSessionMessageInstance(gameData.ticketBeingConsidered
                                                        .getId()));
                                        listener.setSubjectText("Revealing votes...");
                                    }
                                });
                builder.show();
            }
        });

        voteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener.getCardOnTheTable() == null) {
                    Toast.makeText(getActivity(), "Place card on the table first!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Vote vote = new Vote();
                    vote.setCard(listener.getCardOnTheTable().getExternalCard());
                    vote.setPlayer(gameData.getCurrentHandshakenPlayer());
                    vote.setTicketId(gameData.getTicketBeingConsidered().getId());
                    nioHandler.enqueueRequest(RequestFor.SMSimpleVote,
                            gameData.getSessionMessageInstance(vote));
                }
            }
        });

        masterPanel.setVisibility(gameData.amiGameMaster() ? View.VISIBLE : View.GONE);
        participantPanel.setVisibility(gameData.amiGameMaster() ? View.GONE : View.VISIBLE);

        viewCreated = true;
        return rootView;
    }
    
    public void setRevealAreaVisible(boolean visible){
        revealArea.setVisibility(visible?View.VISIBLE:View.GONE);
    }
    
    public void setSubmitTicketAreaVisible(boolean visible){
        submitTicketArea.setVisibility(visible?View.VISIBLE:View.GONE);
    }
    
    public void setVotingAnabled(boolean enabled){
        voteButton.setEnabled(enabled);
    }
    
    public interface GameDashboardListener{
        public void setSubjectText(String text);
        public CardSprite getCardOnTheTable();
    }
}
