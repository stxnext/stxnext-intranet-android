package com.stxnext.management.server.planningpoker.server.dto.combined;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

@DatabaseTable(tableName = Card.ENTITY_NAME)
public class Card extends AbstractMessage{
    
    public static final String ENTITY_NAME = "poker_card";
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "display_value";
    public static final String FIELD_DECK_ID = "deck";
    
    public static final String JSON_DECK_ID = "deck_id";
    
    @Expose
    @SerializedName(FIELD_ID)
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @Expose
    @SerializedName(FIELD_NAME)
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    
    @DatabaseField(foreign = true, columnName = FIELD_DECK_ID)
    private Deck deck;
    
    @Expose
    @SerializedName(JSON_DECK_ID)
    private Long deckId;
    
    public Card(){};

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    @Override
    protected void prepareToSerialization() {
        //nope, no foreign collection here
    }
    
    
}
