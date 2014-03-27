
package com.stxnext.management.android.games.poker;

import java.util.List;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.ui.dependencies.ExtendedViewPager;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;

public interface GameSetupListener {
    public void onRoleChosen(GameRole role);
    public void onSessionJoin();
    public IntranetUser getCurrentUser();
    public List<Team> getTeams();
    public SimpleFragmentAdapter getFragmentAdapter();
    public ExtendedViewPager getViewPager();
    
    public enum GameRole{
        PARTICIPANT,
        MASTER;
    }
}
