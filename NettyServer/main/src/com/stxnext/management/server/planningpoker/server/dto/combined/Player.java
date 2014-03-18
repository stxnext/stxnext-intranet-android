package com.stxnext.management.server.planningpoker.server.dto.combined;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

@DatabaseTable(tableName = Player.ENTITY_NAME)
public class Player extends AbstractMessage {

    public static final String ENTITY_NAME = "poker_player"; 
	// for QueryBuilder to be able to find the fields
    
    public static final String FIELD_ID = "id"; 
	public static final String FIELD_NAME = "name";
	public static final String FIELD_EMAIL = "email";
	public static final String FIELD_EXTERNAL_ID = "external_id";
	public static final String FIELD_TEAM_ID = "team_id";
	public static final String FIELD_ACTIVE = "active";

	
	@Expose
    @SerializedName(FIELD_ID)
	@DatabaseField(generatedId = true, columnName = FIELD_ID)
	private Long id;
	
	@Expose
    @SerializedName(FIELD_NAME)
	@DatabaseField(columnName = FIELD_NAME, canBeNull = false)
	private String name;

	@Expose
    @SerializedName(FIELD_EMAIL)
	@DatabaseField(columnName = FIELD_EMAIL)
	private String email;
	
	@Expose
    @SerializedName(FIELD_EXTERNAL_ID)
	@DatabaseField(columnName = FIELD_EXTERNAL_ID)
    private Long externalId;
	
	@Expose
    @SerializedName(FIELD_TEAM_ID)
	@DatabaseField(columnName = FIELD_TEAM_ID)
    private Long teamId;
	
	@Expose
    @SerializedName(FIELD_ACTIVE)
    @DatabaseField(columnName = FIELD_ACTIVE)
    private boolean active;

	public Player() {
	}

    public Long getId() {
        return id;
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

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj!=null && obj instanceof Player){
            Player player = (Player) obj;
            if(this.id != null && player.getId() != null)
                return this.id.equals(player.getId());
            if(this.externalId != null && player.getExternalId() != null)
                return this.externalId.equals(player.getExternalId());
        }
        return false;
    }

    @Override
    protected void prepareToSerialization() {
        // no foreign collection here
    }
    
	
}