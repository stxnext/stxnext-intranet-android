
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;

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
    Session sessionIamIn;
    Ticket ticketBeingConsidered;
    List<Deck> decks;
    List<Player> livePlayers = new ArrayList<Player>();

    public void clear() {
        userSessions = null;
        sessionToCreate = null;
        currentUser = null;
        decks = null;
    }
    
    public Deck getCurrentSessionDeck(){
        for(Deck deck : decks){
            if(sessionIamIn.getDeckId().equals(deck.getId())){
                return deck;
            }
        }
        return null;
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

    public Session getSessionIamIn() {
        return sessionIamIn;
    }

    public void setSessionIamIn(Session sessionToJoin) {
        this.sessionIamIn = sessionToJoin;
    }
    
    public <T> SessionMessage<T> getSessionMessageInstance(T subject){
        return new SessionMessage<T>(currentHandshakenPlayer.getId(), sessionIamIn.getId(), subject);
    }

    public boolean amiGameMaster() {
        return (sessionIamIn != null && currentHandshakenPlayer != null
                && sessionIamIn.getOwner() != null && currentHandshakenPlayer.getId() == sessionIamIn
                .getOwner().getId());
    }

    public List<Player> getLivePlayers() {
        return livePlayers;
    }

    public void setLivePlayers(List<Player> livePlayers) {
        this.livePlayers = livePlayers;
    }
    
    public void manageLivePlayer(Player player){
        if(player.isActive()){
            if(!livePlayers.contains(player))
                livePlayers.add(player);
        }
        else{
            livePlayers.remove(player);
        }
    }

    public Ticket getTicketBeingConsidered() {
        return ticketBeingConsidered;
    }

    public void setTicketBeingConsidered(Ticket ticketBeingConsidered) {
        this.ticketBeingConsidered = ticketBeingConsidered;
    }
    
    
}
