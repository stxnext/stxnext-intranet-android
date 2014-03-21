
package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

public enum RequestFor {
    //simple data management
    CardDecks("card_decks"),
    CreateSession("create_session"),
    //preparing for session, querying user sessions, adding user and joining
    SessionForPlayer("player_sessions"),
    PlayerHandshake("player_handshake"),
    PlayersInLiveSession("player_in_live_session"),
    JoinSession("join_session"),
    //live game messages
    SMNewTicketRound("new_ticket_round"),
    SMSimpleVote("simple_vote"),
    SMRevealVotes("reveal_votes"),
    SMFinishSession("finish_session");

    // TODO : need ticket list fetch request to get back to any and set up another voting
    
    private String message;

    RequestFor(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    public static RequestFor requestForMessage(String message){
        RequestFor result = null;
        for(RequestFor req : RequestFor.values()){
            if(req.getMessage().equals(message)){
                result = req;
                break;
            }
        }
        return result;
    }
}
