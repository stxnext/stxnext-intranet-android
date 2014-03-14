
package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

public enum RequestFor {
    CardDecks("card_decks"),
    OngoingSession("ongoing_sessions");

    private String message;

    RequestFor(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
