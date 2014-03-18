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

@DatabaseTable(tableName = Ticket.ENTITY_NAME)
public class Ticket extends AbstractMessage {

    public static final String ENTITY_NAME = "poker_ticket"; 
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_DISPLAY_VALUE = "display_value";
    public static final String JSON_FIELD_VOTES = "votes";
    public static final String FIELD_SESION_ID = "session";
    
    public static final String JSON_SESSION_ID = "session_id";
    public static final String JSON_SESSION = "session";
    
    public Ticket(){};
    
    @Expose
    @SerializedName(FIELD_ID)
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private Long id;
    
    @ForeignCollectionField
    private ForeignCollection<Vote> votes;
    
    @Expose
    @SerializedName(JSON_FIELD_VOTES)
    private List<Vote> jsonVotes;
    
    @Expose
    @SerializedName(JSON_SESSION)
    @DatabaseField(foreign = true, foreignAutoRefresh = true,columnName = FIELD_SESION_ID)
    private Session session;
    
    @Expose
    @SerializedName(JSON_SESSION_ID)
    private Long SessionId;
    
    @Expose
    @SerializedName(FIELD_DISPLAY_VALUE)
    @DatabaseField(columnName = FIELD_DISPLAY_VALUE)
    private String displayValue;

    public long getId() {
        return id;
    }

    public Long getSessionId() {
        return SessionId;
    }

    public void setSessionId(Long sessionId) {
        SessionId = sessionId;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public Session getSession() {
        return session;
    }
    
    public List<Vote> getVotes() {
        if(jsonVotes == null)
            prepareToSerialization();
        return jsonVotes;
    }

    public void setVotes(List<Vote> jsonVotes) {
        this.jsonVotes = jsonVotes;
    }

    @Override
    protected void prepareToSerialization() {
        if(this.votes!=null)
            this.jsonVotes = new ArrayList<Vote>(votes);
    }
    
}
