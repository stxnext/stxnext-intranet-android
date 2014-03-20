
package com.stxnext.management.android.games.poker;

import java.util.List;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.Team;

public interface GameSetupListener {
    public void onRoleChosen(GameRole role);
    public IntranetUser getCurrentUser();
    public List<Team> getTeams();
    
    public enum GameRole{
        PARTICIPANT,
        MASTER;
    }
}
