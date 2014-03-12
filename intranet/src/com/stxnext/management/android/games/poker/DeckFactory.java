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
       int cardIndex = 0;
       
       result.add(createCard(cardPos, cardY, texture, activity, "0", 0f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "1/2", 0.5f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "1", 1f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "2", 2f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "3", 3f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "5", 5f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "8", 8f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "13", 13f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "20", 20f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "40", 40f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "100", 100f,cardIndex++));
       result.add(createCard(cardPos+=cardOffset, cardY, texture, activity, "?", -1f,cardIndex++));
       
       return result;
   }
   
   private static CardSprite createCard(float x, float y, TextureRegion texture, BoardGameActivity activity, String displayValue, Float value, int cardIndex){
       final CardSprite sprite = new CardSprite(x, y, texture,
               activity.getVertexBufferObjectManager(),activity, cardIndex);
       sprite.prepare(displayValue, value);
       return sprite;
   }
    
}
