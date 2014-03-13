
package com.stxnext.management.server.planningpoker.server.database.dto;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Deck.ENTITY_NAME)
public class Deck {
    public static final String ENTITY_NAME = "poker_deck";
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    
    @ForeignCollectionField
    private ForeignCollection<Card> cards;

    public Deck(){}
    
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ForeignCollection<Card> getCards() {
        return cards;
    }

    public void setCards(ForeignCollection<Card> cards) {
        this.cards = cards;
    }
    
}
