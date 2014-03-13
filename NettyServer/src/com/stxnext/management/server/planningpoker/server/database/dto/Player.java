package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Player.ENTITY_NAME)
public class Player {

    public static final String ENTITY_NAME = "player"; 
	// for QueryBuilder to be able to find the fields
    
    public static final String FIELD_ID = "id"; 
	public static final String FIELD_NAME = "name";
	public static final String FIELD_EMAIL = "email";
	public static final String FIELD_EXTERNAL_ID = "external_id";
	public static final String FIELD_TEAM_ID = "team_id";

	@DatabaseField(generatedId = true, columnName = FIELD_ID)
	private long id;

	@DatabaseField(columnName = FIELD_NAME, canBeNull = false)
	private String name;

	@DatabaseField(columnName = FIELD_EMAIL)
	private String email;
	
	@DatabaseField(columnName = FIELD_EXTERNAL_ID)
    private long externalId;
	
	@DatabaseField(columnName = FIELD_TEAM_ID)
    private long teamId;

	Player() {
	}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long externalId) {
        this.externalId = externalId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
	
}