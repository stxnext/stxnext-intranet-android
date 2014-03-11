package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import org.andengine.opengl.texture.region.TextureRegion;

public class DeckFactory {

    public enum DeckType{
        DEFAULT,
        FIBONACCI
    }
    
    
   public static List<CardSprite> produce(DeckType type, TextureRegion texture, BoardGameActivity activity){
       List<CardSprite> result = new ArrayList<CardSprite>();
       int cardOffset = BoardGameActivity.CAMERA_WIDTH/12;
       int cardY = (int) (BoardGameActivity.CAMERA_HEIGHT - (CardSprite.CARD_HEIGHT));
       int cardPos = 0;
       
       result.add(createCard(cardPos, cardY, texture, activity, "0", 0f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "1/2", 0.5f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "1", 1f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "2", 2f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "3", 3f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "5", 5f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "8", 8f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "13", 13f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "20", 20f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "40", 40f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "100", 100f));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "?", -1f));
       
       return result;
   }
   
   private static CardSprite createCard(float x, float y, TextureRegion texture, BoardGameActivity activity, String displayValue, Float value){
       final CardSprite sprite = new CardSprite(x, y, texture,
               activity.getVertexBufferObjectManager(),activity);
       sprite.prepare(displayValue, value);
       return sprite;
   }
    
}
