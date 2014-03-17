
package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

public enum RequestFor {
    CardDecks("card_decks"),
    OngoingSession("ongoing_sessions"),
    CreateSession("create_session"),
    SessionForPlayer("player_sessions"),
    PlayerHandshake("player_handshake"),
    PlayersInSession("player_in_session"),
    JoinSession("join_session"),
    SMNewTicketRound("new_ticket_round"),
    SMSimpleVote("simple_vote"),
    SMRevealVotes("reveal_votes"),
    SMFinishSession("finish_session");

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
