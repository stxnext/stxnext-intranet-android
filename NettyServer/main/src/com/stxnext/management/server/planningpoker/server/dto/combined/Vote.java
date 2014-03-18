
package com.stxnext.management.server.planningpoker.server.dto.combined;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

@DatabaseTable(tableName = Vote.ENTITY_NAME)
public class Vote extends AbstractMessage {

    public static final String ENTITY_NAME = "poker_vote";

    public static final String FIELD_ID = "id";
    public static final String FIELD_CARD_ID = "card_id";
    public static final String FIELD_PLAYER_ID = "player_id";
    public static final String FIELD_TICKET_ID = "ticket_id";
    
    @Expose
    @SerializedName(FIELD_ID)
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private Long id;

    @Expose
    @SerializedName(FIELD_CARD_ID)
    @DatabaseField(foreign = true, foreignAutoRefresh = true,columnName = FIELD_CARD_ID)
    private Card card;
    
    @Expose
    @SerializedName(FIELD_PLAYER_ID)
    @DatabaseField(foreign = true,foreignAutoRefresh = true, columnName = FIELD_PLAYER_ID)
    private Player player;
    
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_TICKET_ID)
    private Ticket ticket;
    
    @Expose
    @SerializedName(FIELD_TICKET_ID)
    private Long ticketId;

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
    
    public Long getTicketId() {
        prepareToSerialization();
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    protected void prepareToSerialization() {
        if(ticketId == null){
            if(ticket!=null){
                ticketId = ticket.getId();
            }
        }
    }

}
