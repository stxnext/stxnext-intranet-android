package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.games.poker.SetupActivity.SetupActivityListener;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.ui.dependencies.Popup;
import com.stxnext.management.android.ui.dependencies.Popup.OnPopupItemClickListener;
import com.stxnext.management.android.ui.dependencies.PopupItem;
import com.stxnext.management.android.ui.dependencies.UserListAdapter;

public class SetupSessionFragment extends Fragment implements SetupActivityListener{

    private View view;
    EditText sessionNameView;
    TextView teamSelector;
    Cursor usersCursor;
    
    private Popup teamsPopup;
    
    GameSetupListener listener;
    
    ListView participantsListView;
    UserListAdapter participantsAdapter;
    Team selectedTeam;
    
    public SetupSessionFragment(GameSetupListener listener){
        super();
        this.listener = listener;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }
    
    
    private boolean popupSetup;
    private void setupPopupTeams(){
        if(popupSetup)
            return;
        List<Team> teams = listener.getTeams();
        List<PopupItem> popupItems = new ArrayList<PopupItem>();
        for(Team team : teams){
            popupItems.add(new PopupItem(team.getName(), team));
        }
        
        teamsPopup = new Popup(getActivity(), teamSelector);
        teamsPopup.addItems(popupItems);
        teamsPopup.setSelected(teams.get(0));
        
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
                teamsPopup.toggle();
            }
        });
        popupSetup = true;
    }
    
    boolean viewCreated;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setup_session, container,
                false);
        sessionNameView = (EditText) view.findViewById(R.id.sessionNameView);
        teamSelector = (TextView) view.findViewById(R.id.teamSelector);
        participantsListView = (ListView) view.findViewById(R.id.usersList);
        viewCreated = true;
        setFormEnabled(formsEnabled);
        return view;
    }
    
    private void setUpList(){
        Cursor c = DAO.getInstance().getIntranetUser().getCursorByTeamId(selectedTeam.getId().longValue());
        int count = c.getCount();
        Log.e("","result count :"+count);
        if(participantsAdapter == null){
            usersCursor = c;
            getActivity().startManagingCursor(c);
            participantsAdapter = new UserListAdapter(getActivity(), c, participantsListView);
            participantsAdapter.setDontUseCursorCache(true);
            participantsListView.setAdapter(participantsAdapter);
            participantsAdapter.notifyDataSetChanged();
        }
        else{
            getActivity().stopManagingCursor(usersCursor);
            getActivity().startManagingCursor(c);
            usersCursor = c;
            participantsAdapter.changeCursor(c);
            participantsAdapter.notifyDataSetChanged();
            //participantsListView.invalidateViews();
        }
    }

    boolean formsEnabled = false;
    @Override
    public void setFormEnabled(boolean enabled) {
        formsEnabled = enabled;
        if(!viewCreated)
            return;
        if(enabled){
            setupPopupTeams();
        }
    }

    
}