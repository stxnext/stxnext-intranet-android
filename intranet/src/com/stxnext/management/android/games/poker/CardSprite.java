
package com.stxnext.management.android.games.poker;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

public class CardSprite extends Sprite {

    private static Font font;
    private static Sound cardPickSound;
    private static Sound cardPutSound;

    public static final int CARD_WIDTH = 116;
    public static final int CARD_HEIGHT = 170;

    private BoardGameActivity gameActivity;
    //public final static float cardGlobalScale = 0.4f;
    boolean mGrabbed = false;
    Float value;
    String displayValue;
    
    float originalX;
    float originalY;

    public CardSprite(float pX, float pY, TextureRegion pTiledTextureRegion,
            VertexBufferObjectManager pVertexBufferObjectManager, BoardGameActivity gameActivity) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        originalX = pX;
        originalY = pY;
        this.gameActivity = gameActivity;
    }

    public void backToOrigilanPosition(){
        
    }

    public void prepare(String displayValue, Float value) {
        final VertexBufferObjectManager vertexBufferObjectManager = gameActivity
                .getVertexBufferObjectManager();
        final Text centerText = new Text(0, 0, font, displayValue, new TextOptions(
                HorizontalAlign.CENTER), vertexBufferObjectManager);
        float textX = ((CARD_WIDTH / 2) - (centerText.getWidth() / 2));
        float textY = ((CARD_HEIGHT / 2) - (centerText.getHeight() / 2));

        centerText.setX(textX);
        centerText.setY(textY);
        attachChild(centerText);
        
        final Text leftTopText = new Text(5, 5, font, displayValue, new TextOptions(
                HorizontalAlign.CENTER), vertexBufferObjectManager);
        leftTopText.setScale(0.6f);
        attachChild(leftTopText);
        
        
        final Text rightTopText = new Text(0,5, font, displayValue, new TextOptions(
                HorizontalAlign.CENTER), vertexBufferObjectManager);
        rightTopText.setX(CARD_WIDTH - rightTopText.getWidthScaled() - 5);
        rightTopText.setScale(0.6f);
        attachChild(rightTopText);
        
        
        final Text leftBottomText = new Text(5, 0, font, displayValue, new TextOptions(
                HorizontalAlign.CENTER), vertexBufferObjectManager);
        leftBottomText.setY(CARD_HEIGHT - leftBottomText.getHeightScaled() - 5);
        leftBottomText.setScale(0.6f);
        attachChild(leftBottomText);
        
        
        final Text rightBottomText = new Text(0, 0, font, displayValue, new TextOptions(
                HorizontalAlign.CENTER), vertexBufferObjectManager);
        rightBottomText.setY(CARD_HEIGHT - rightBottomText.getHeightScaled() - 5);
        rightBottomText.setX(CARD_WIDTH - rightBottomText.getWidthScaled() - 5);
        rightBottomText.setScale(0.6f);
        attachChild(rightBottomText);
        
        this.value = value;
    }
    
  

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        switch (pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                this.setScale(1.25f);
                this.mGrabbed = true;
                cardPickSound.play();
                gameActivity.clearCardsZIndex();
                this.setZIndex(this.getZIndex() + 1);
                getParent().sortChildren();
                break;
            case TouchEvent.ACTION_MOVE:
                if (this.mGrabbed) {
                    this.setPosition(pSceneTouchEvent.getX() - CARD_WIDTH / 2,
                            pSceneTouchEvent.getY() - CARD_HEIGHT / 2);
                }
                break;
            case TouchEvent.ACTION_UP:
                if (this.mGrabbed) {
                    cardPutSound.play();
                    this.mGrabbed = false;
                    this.setScale(1.0f);
                }
                break;
        }
        return true;
    }

    
    
    public static void setCardPickSound(Sound cardPickSound) {
        CardSprite.cardPickSound = cardPickSound;
    }

    public static void setCardPutSound(Sound cardPutSound) {
        CardSprite.cardPutSound = cardPutSound;
    }
    
    public static void setFont(Font font) {
        CardSprite.font = font;
    }

    public Float getValue() {
        return value;
    }

    public float getOriginalX() {
        return originalX;
    }

    public float getOriginalY() {
        return originalY;
    }
    
    

}
