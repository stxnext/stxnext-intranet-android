
package com.stxnext.management.server.planningpoker.server.dto.messaging.out;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

public class DeckSetMessage extends AbstractMessage {

    public static final String FILED_DECKS = "decks";

    @Expose
    @SerializedName(FILED_DECKS)
    private List<Deck> decks;

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    @Override
    public void prepareToSerialization() {
        if(decks!=null){
            for(Deck deck : decks){
                deck.prepareToSerialization();
            }
        }
    }
    
}
