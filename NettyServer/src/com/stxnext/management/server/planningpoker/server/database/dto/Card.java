package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Card.ENTITY_NAME)
public class Card {
    
    public static final String ENTITY_NAME = "card";
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "display_value";
    
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @DatabaseField(columnName = FIELD_NAME)
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
