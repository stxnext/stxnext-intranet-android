
package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Vote.ENTITY_NAME)
public class Vote {

    public static final String ENTITY_NAME = "poker_vote";

    public static final String FIELD_ID = "id";
    public static final String FIELD_CARD_ID = "card_id";
    public static final String FIELD_PLAYER_ID = "player_id";
    public static final String FIELD_TICKET_ID = "ticket_id";
    
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true,columnName = FIELD_CARD_ID)
    private Card card;
    
    @DatabaseField(foreign = true,foreignAutoRefresh = true, columnName = FIELD_PLAYER_ID)
    private Player player;
    
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_TICKET_ID)
    private Ticket ticket;

    public Vote(){};
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

}
