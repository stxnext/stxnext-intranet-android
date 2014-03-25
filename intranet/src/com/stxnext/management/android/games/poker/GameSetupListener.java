
package com.stxnext.management.android.games.poker;

import java.util.List;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.ui.dependencies.ExtendedViewPager;

public interface GameSetupListener {
    public void onRoleChosen(GameRole role);
    public IntranetUser getCurrentUser();
    public List<Team> getTeams();
    public SetupFragmentAdapter getFragmentAdapter();
    public ExtendedViewPager getViewPager();
    
    public enum GameRole{
        PARTICIPANT,
        MASTER;
    }
}
