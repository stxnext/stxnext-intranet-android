
package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Vote.ENTITY_NAME)
public class Vote {

    public static final String ENTITY_NAME = "vote";

    public static final String FIELD_ID = "id";
    public static final String FIELD_CARD_ID = "card_id";
    public static final String FIELD_PLAYER_ID = "player_id";
    
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(foreign = true, columnName = FIELD_CARD_ID)
    private Card card;
    
    @DatabaseField(foreign = true, columnName = FIELD_PLAYER_ID)
    private Player player;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
