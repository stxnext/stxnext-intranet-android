
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

@DatabaseTable(tableName = Deck.ENTITY_NAME)
public class Deck extends AbstractMessage {
    public static final String ENTITY_NAME = "poker_deck";
    
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PREDEFINED_TYPE = "predefined_type";
    public static final String JSON_FIELD_CARDS = "cards";
    
    @Expose
    @SerializedName(FIELD_ID)
    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;
    
    @Expose
    @SerializedName(FIELD_NAME)
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    
    @Expose
    @SerializedName(FIELD_PREDEFINED_TYPE)
    @DatabaseField(columnName = FIELD_PREDEFINED_TYPE)
    private int predefinedType;
    
    @ForeignCollectionField
    private ForeignCollection<Card> cards;
    
    @Expose
    @SerializedName(JSON_FIELD_CARDS)
    private List<Card> jsonCards;

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

    public int getPredefinedType() {
        return predefinedType;
    }

    public void setPredefinedType(int predefinedType) {
        this.predefinedType = predefinedType;
    }
    
    public List<Card> getCards() {
        if(jsonCards == null)
            prepareToSerialization();
        return jsonCards;
    }

    public void setCards(List<Card> jsonCards) {
        this.jsonCards = jsonCards;
    }

    @Override
    public void prepareToSerialization() {
        if(this.cards != null)
            this.jsonCards = new ArrayList<Card>(this.cards);
    }
    
}
