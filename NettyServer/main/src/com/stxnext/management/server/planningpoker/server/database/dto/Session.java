package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Session.ENTITY_NAME)
public class Session {
    
    public static final String ENTITY_NAME = "poker_session"; 
    
    public static final String FIELD_ID = "id"; 
    public static final String FIELD_START_TIME = "start_time";
    public static final String FIELD_END_TIME = "end_time";
    public static final String FIELD_EXPIRED = "expired";

    public Session(){}

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @DatabaseField(columnName = FIELD_START_TIME)
    private long startTime;
    
    @DatabaseField(columnName = FIELD_END_TIME)
    private long endTime;
    
    @DatabaseField(columnName = FIELD_EXPIRED)
    private boolean expired;
    
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
