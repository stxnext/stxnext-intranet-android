package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import org.andengine.opengl.texture.region.TextureRegion;

import com.stxnext.management.server.planningpoker.server.dto.combined.Card;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;

public class DeckFactory {

   public static List<CardSprite> produce(Deck deck, TextureRegion texture, BoardGameActivity activity){
       List<CardSprite> result = new ArrayList<CardSprite>();
       int cardOffset = BoardGameActivity.CAMERA_WIDTH/12;
       int cardY = (int) (BoardGameActivity.CAMERA_HEIGHT - (CardSprite.CARD_HEIGHT));
       int cardPos = 0;
       int cardIndex = 0;
       
       for(Card card : deck.getCards()){
           result.add(createCard(cardPos, cardY, texture, activity, card.getName(),card.getId(),cardIndex++));
           cardPos+=cardOffset;
       }
       return result;
   }
   
   private static CardSprite createCard(float x, float y, TextureRegion texture, BoardGameActivity activity, String displayValue, Long externalId, int cardIndex){
       final CardSprite sprite = new CardSprite(x, y, texture,
               activity.getVertexBufferObjectManager(),activity, cardIndex);
       sprite.prepare(displayValue, externalId);
       return sprite;
   }
    
}
