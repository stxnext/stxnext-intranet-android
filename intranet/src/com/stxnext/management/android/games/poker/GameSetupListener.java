
package com.stxnext.management.android.games.poker;

import com.stxnext.management.android.dto.local.IntranetUser;

public interface GameSetupListener {
    public void onRoleChosen(GameRole role);
    public IntranetUser getCurrentUser();
    
    public enum GameRole{
        PARTICIPANT,
        MASTER;
    }
}
