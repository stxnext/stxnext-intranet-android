
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;

public class OSDMenu {

    OSDMenuListener listener;
    BaseGameActivity activity;

    TextureRegion alignDeckTexture;
    private Font mFont;
    List<MenuOptionSprite> menuOptions;

    public OSDMenu(BaseGameActivity activity,OSDMenuListener listener,TextureRegion alignDeckTexture) {
        this.activity = activity;
        this.listener = listener;
        this.alignDeckTexture = alignDeckTexture;
        this.menuOptions = new ArrayList<OSDMenu.MenuOptionSprite>();
    }
    
    public void setVisible(boolean visible){
        for(MenuOptionSprite sprite : menuOptions){
            sprite.setVisible(visible);
        }
    }

    public void prepareTextures() {
        this.mFont = FontFactory.create(activity.getFontManager(),
                activity.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 18,Color.WHITE);
        mFont.load();
    }

    public void prepareScene(Scene scene) {
        MenuOptionSprite menuAlign = new MenuOptionSprite(50, 50, alignDeckTexture,
                activity.getVertexBufferObjectManager(), new Runnable() {
                    @Override
                    public void run() {
                        listener.onAlignDeck();
                    }
                });
        menuAlign.prepare("Align Deck");
        scene.attachChild(menuAlign);
        scene.registerTouchArea(menuAlign);
        menuOptions.add(menuAlign);
    }

    public interface OSDMenuListener {
        public void onAlignDeck();
    }

    private class MenuOptionSprite extends Sprite {

        Runnable action;
        Text centerText;
        
        public MenuOptionSprite(float pX, float pY,
                ITextureRegion pTextureRegion, VertexBufferObjectManager vertexBuffer, Runnable action) {
            super(pX, pY, pTextureRegion, vertexBuffer);
            this.action = action;
        }
        
        private void prepare(String optionName) {
            final VertexBufferObjectManager vertexBufferObjectManager = activity
                    .getVertexBufferObjectManager();
            centerText = new Text(0, 0, mFont, optionName, new TextOptions(
                    HorizontalAlign.CENTER), vertexBufferObjectManager);
            float textX = ((getWidth() / 2) - (centerText.getWidth() / 2));
            float textY = getHeight() + 10;
            centerText.setX(textX);
            centerText.setY(textY);
            attachChild(centerText);
        }

        @Override
        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
                float pTouchAreaLocalY) {
            switch (pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    this.setScale(1.55f);
                    break;
                case TouchEvent.ACTION_UP:
                    if (action != null) {
                        action.run();
                    }
                    this.setScale(1.0f);
                    break;
            }
            return true;
        }

    }

}
