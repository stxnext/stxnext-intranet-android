package com.stxnext.management.server.planningpoker.server.dto.combined;

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
    public static final String FIELD_START_TIME = "start_time";
    public static final String FIELD_END_TIME = "end_time";
    public static final String FIELD_EXPIRED = "expired";
    
    public static final String JSON_FIELD_PLAYERS = "players";

    public Session(){}

    @Expose
    @SerializedName(FIELD_ID)
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @Expose
    @SerializedName(FIELD_START_TIME)
    @DatabaseField(columnName = FIELD_START_TIME)
    private long startTime;
    
    @Expose
    @SerializedName(FIELD_END_TIME)
    @DatabaseField(columnName = FIELD_END_TIME)
    private long endTime;
    
    @Expose
    @SerializedName(FIELD_EXPIRED)
    @DatabaseField(columnName = FIELD_EXPIRED)
    private boolean expired;
    
    @Expose
    @SerializedName(JSON_FIELD_PLAYERS)
    @ForeignCollectionField
    private ForeignCollection<Player> players;

    public long getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public ForeignCollection<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ForeignCollection<Player> players) {
        this.players = players;
    }
    
}
