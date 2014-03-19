package com.stxnext.management.server.planningpoker.server.dto.combined;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

@DatabaseTable(tableName = Session.ENTITY_NAME)
public class Session  extends AbstractMessage{
    
    public static final String ENTITY_NAME = "poker_session"; 
    
    public static final String FIELD_ID = "id"; 
    public static final String FIELD_NAME = "name";
    public static final String FIELD_START_TIME = "start_time";
    public static final String FIELD_END_TIME = "end_time";
    public static final String FIELD_EXPIRED = "expired";
    public static final String FIELD_OWNER_ID = "owner";
    public static final String FIELD_DECK_ID = "deck_id";
    
    public static final String JSON_FIELD_TICKETS = "tickets";
    public static final String JSON_FIELD_PLAYERS = "players";

    public Session(){}

    @Expose
    @SerializedName(FIELD_ID)
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private Long id;
    
    @Expose
    @SerializedName(FIELD_START_TIME)
    @DatabaseField(columnName = FIELD_START_TIME)
    private long startTime;
    
    @Expose
    @SerializedName(FIELD_NAME)
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    
    @Expose
    @SerializedName(FIELD_DECK_ID)
    @DatabaseField(columnName = FIELD_DECK_ID)
    private Long deckId;
    
    @Expose
    @SerializedName(FIELD_END_TIME)
    @DatabaseField(columnName = FIELD_END_TIME)
    private long endTime;
    
    @Expose
    @SerializedName(FIELD_EXPIRED)
    @DatabaseField(columnName = FIELD_EXPIRED)
    private boolean expired;
    
    @Expose
    @SerializedName(FIELD_OWNER_ID)
    @DatabaseField(foreign = true, foreignAutoRefresh = true,columnName = FIELD_OWNER_ID)
    private Player owner;
    
    @ForeignCollectionField
    private ForeignCollection<Ticket> tickets;
    
    @Expose
    @SerializedName(JSON_FIELD_TICKETS)
    private List<Ticket> jsonTickets;
    
    @Expose
    @SerializedName(JSON_FIELD_PLAYERS)
    private List<Player> players;

    public long getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public void addTicket(Ticket ticket){
        if(this.jsonTickets == null)
            this.jsonTickets = new ArrayList<Ticket>();
        
        this.jsonTickets.add(ticket);
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public List<Ticket> getTickets() {
        if(jsonTickets == null)
            prepareToSerialization();
        return jsonTickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.jsonTickets = tickets;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void prepareToSerialization(){
        if(this.tickets !=null)
            this.jsonTickets = new ArrayList<Ticket>(this.tickets);
    }
    
    
}
