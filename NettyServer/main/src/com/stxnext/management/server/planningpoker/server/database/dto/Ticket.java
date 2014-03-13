package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Ticket.ENTITY_NAME)
public class Ticket {

    public static final String ENTITY_NAME = "poker_ticket"; 
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_DISPLAY_VALUE = "display_value";
    
    public Ticket(){};
    
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @ForeignCollectionField
    private ForeignCollection<Vote> votes;
    
    @DatabaseField(columnName = FIELD_DISPLAY_VALUE)
    private String displayValue;

    public long getId() {
        return id;
    }

    public ForeignCollection<Vote> getVotes() {
        return votes;
    }

    public void setVotes(ForeignCollection<Vote> votes) {
        this.votes = votes;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
}
