package com.stxnext.management.android.dto.local;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;

public class TeamResult extends AbstractMessage{

    @Expose
    @SerializedName("teams")
    List<Team> teams = new ArrayList<Team>();

    public List<Team> getTeams() {
        return teams;
    }
    
    public TeamResult addTeam(Team team){
        teams.add(team);
        return this;
    }
    
}
