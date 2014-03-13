package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Card.ENTITY_NAME)
public class Card {
    
    public static final String ENTITY_NAME = "poker_card";
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "display_value";
    public static final String FIELD_DECK_ID = "deck_id";
    
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    
    @DatabaseField(foreign = true, columnName = FIELD_DECK_ID)
    private Deck deck;
    
    public Card(){};

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
    
    
}
