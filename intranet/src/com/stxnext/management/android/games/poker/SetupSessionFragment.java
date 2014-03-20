package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.postmessage.AbsenceMessage.AbsenceType;
import com.stxnext.management.android.games.poker.SetupActivity.SetupActivityListener;
import com.stxnext.management.android.ui.dependencies.Popup;
import com.stxnext.management.android.ui.dependencies.PopupItem;
import com.stxnext.management.android.ui.dependencies.Popup.OnPopupItemClickListener;

public class SetupSessionFragment extends Fragment implements SetupActivityListener {

    private View view;
    EditText sessionNameView;
    TextView teamSelector;
    
    private Popup teamsPopup;
    
    GameSetupListener listener;
    
    public SetupSessionFragment(GameSetupListener listener){
        super();
        this.listener = listener;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }
    
    private void setupPopupTeams(){
        List<PopupItem> items = new ArrayList<PopupItem>();
        for(AbsenceType type : AbsenceType.values()){
            items.add(new PopupItem(getString(type.getResourceId()), type));
        }
        teamsPopup = new Popup(getActivity(), absenceTypeView);
        typePopup.addItems(items);
        typePopup.setSelected(AbsenceType.PLANNED);
        
        typePopup.setOnItemClickListener(new OnPopupItemClickListener() {
            @Override
            public void onItemClick(Object content) {
                typePopup.setSelected(content);
            }
        });
        typeRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                typePopup.toggle();
            }
        });
    }
    
    boolean viewCreated;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setup_session, container,
                false);
        
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
            //setup teams picker here
        }
    }
    
    
}