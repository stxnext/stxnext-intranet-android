
package com.stxnext.management.android.games.poker;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseBackInOut;
import org.andengine.util.modifier.ease.EaseCubicInOut;
import org.andengine.util.modifier.ease.EaseElasticIn;
import org.andengine.util.modifier.ease.EaseElasticInOut;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.EaseStrongOut;

public class CardSprite extends Sprite {

    private static Font font;
    private static Sound cardPickSound;
    private static Sound cardPutSound;

    public static final int CARD_WIDTH = 116;
    public static final int CARD_HEIGHT = 170;

    MoveModifier moveModifier;
    ScaleModifier scaleModifier;
    private BoardGameActivity gameActivity;
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
        this.moveModifier = new MoveModifier(0.1f, pX, pX, pY, pY, EaseLinear.getInstance());
        this.scaleModifier = new ScaleModifier(0.1f, 1f, 1f, EaseCubicInOut.getInstance());
    }

    public void backToOrigilanPosition(int index) {
        registerEntityModifier(new MoveModifier(1f, getX(), getOriginalX(), getY(), getOriginalY(), EaseStrongOut.getInstance()));
        setZIndex(index);
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

        final Text rightTopText = new Text(0, 5, font, displayValue, new TextOptions(
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
                //this.setScale(1.25f);
                this.scaleModifier.reset(0.2f, 1f, 1.5f,1f,1.5f);
                this.registerEntityModifier(scaleModifier);
                this.mGrabbed = true;
                cardPickSound.play();
                gameActivity.clearCardsZIndex();
                this.setZIndex(this.getZIndex() + 1);
                getParent().sortChildren();
                break;
            case TouchEvent.ACTION_MOVE:
                if (this.mGrabbed) {
                    this.moveModifier.reset(0.12f, getX(), pSceneTouchEvent.getX() - CARD_WIDTH / 2,
                            getY(), pSceneTouchEvent.getY() - CARD_HEIGHT / 2);
                    this.registerEntityModifier(moveModifier);
                }
                break;
            case TouchEvent.ACTION_UP:
                if (this.mGrabbed) {
                    cardPutSound.play();
                    this.mGrabbed = false;
                    this.scaleModifier.reset(0.2f, 1.5f, 1f,1.5f,1f);
                    this.registerEntityModifier(scaleModifier);
                    //this.setScale(1.0f);
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
