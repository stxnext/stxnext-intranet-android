package com.stxnext.management.android.games.poker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.stxnext.management.android.R;
import com.stxnext.management.android.games.poker.GameSetupListener.GameRole;
import com.stxnext.management.android.games.poker.SetupActivity.SetupActivityListener;

public class SetupRoleFragment  extends SherlockFragment implements SetupActivityListener {

    private View view;
    Button masterButton;
    Button playerButton;
    TextView userNameView;
    
    GameSetupListener listener;
    
    public SetupRoleFragment(GameSetupListener listener){
        super();
        this.listener = listener;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);
    }
    
    
    boolean viewCreated;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_role_pick, container,
                false);
        
        masterButton = (Button) view.findViewById(R.id.masterButton);
        playerButton = (Button) view.findViewById(R.id.playerButton);
        userNameView = (TextView) view.findViewById(R.id.userNameView);
        
        userNameView.setText(null);
        
        masterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRoleChosen(GameRole.MASTER);
            }
        });
        
        playerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRoleChosen(GameRole.PARTICIPANT);
            }
        });
        viewCreated = true;
        setFormEnabled(formsEnabled);
        return view;
    }

    
    boolean formsEnabled = false;
    @Override
    public void setFormEnabled(boolean enabled) {
        formsEnabled = enabled;
        if(!viewCreated)
            return;
        if(enabled){
            userNameView.setText(this.listener.getCurrentUser().getName());
        }
        masterButton.setEnabled(enabled);
        playerButton.setEnabled(enabled);
    }
}
