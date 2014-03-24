
package com.stxnext.management.android.games.poker;

import java.util.List;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;

public class GameData {

    private static GameData _instance;

    public static GameData getInstance() {
        if (_instance == null) {
            _instance = new GameData();
        }
        return _instance;
    }

    private GameData() {
    }

    List<Session> userSessions;
    Session sessionToCreate;
    IntranetUser currentUser;
    Player currentHandshakenPlayer;
    List<Deck> decks;

    public void clear() {
        userSessions = null;
        sessionToCreate = null;
        currentUser = null;
        decks = null;
    }

    public List<Session> getUserSessions() {
        return userSessions;
    }

    public void setUserSessions(List<Session> userSessions) {
        this.userSessions = userSessions;
    }

    public Session getSessionToCreate() {
        return sessionToCreate;
    }

    public void setSessionToCreate(Session sessionToCreate) {
        this.sessionToCreate = sessionToCreate;
    }

    public IntranetUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(IntranetUser currentUser) {
        this.currentUser = currentUser;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public Player getCurrentHandshakenPlayer() {
        return currentHandshakenPlayer;
    }

    public void setCurrentHandshakenPlayer(Player currentHandshakenPlayer) {
        this.currentHandshakenPlayer = currentHandshakenPlayer;
    }
    
    
    
}
