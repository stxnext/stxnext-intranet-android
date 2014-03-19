package com.stxnext.management.android.games.poker;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SetupPlayersFragment extends Fragment{

        private View view;
        Button masterButton;
        Button playerButton;
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setRetainInstance(true);
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_role_pick, container,
                    false);
            
            masterButton = (Button) view.findViewById(R.id.masterButton);
            playerButton = (Button) view.findViewById(R.id.playerButton);
            
            return view;
        }
        
    

    
}
