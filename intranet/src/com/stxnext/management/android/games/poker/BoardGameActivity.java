
package com.stxnext.management.android.games.poker;

import java.io.IOException;
import java.util.List;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicInOut;

import android.graphics.Typeface;
import android.view.Display;

import com.stxnext.management.android.games.poker.DeckFactory.DeckType;
import com.stxnext.management.android.games.poker.OSDMenu.OSDMenuListener;

public class BoardGameActivity extends SimpleBaseGameActivity implements OSDMenuListener {
    // ===========================================================
    // Constants
    // ===========================================================

    public static int CAMERA_HEIGHT = 720;
    public static int CAMERA_WIDTH = 480;

    // ===========================================================
    // Fields
    // ===========================================================

    private Camera mCamera;
    private Scene mScene;
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mCardTextureRegion;
    private TextureRegion mDeskTextureRegion;
    private Sprite deskSprite;
    private Font mFont;
    private Sound cardPickSound;
    private Sound cardPutSound;
    private RepeatingSpriteBackground tableTexture;
    private OSDMenu osdMenu;
    private CardSprite draggedCardSprite;
    private CardSprite cardOnTheTable;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    
    @SuppressWarnings("deprecation")//need to support gingerbread
    @Override
    public EngineOptions onCreateEngineOptions() {

        Display display = getWindowManager().getDefaultDisplay();
        CAMERA_WIDTH = display.getWidth();
        CAMERA_HEIGHT = display.getHeight();

        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(true,
                ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH,
                        CAMERA_HEIGHT), this.mCamera);
        engineOptions.getRenderOptions().setMultiSampling(true);
        engineOptions.getAudioOptions().setNeedsSound(true);
        engineOptions.getRenderOptions().setDithering(true);
        engineOptions.getTouchOptions().setNeedsMultiTouch(false);

        return engineOptions;
    }

    // kind of complicated and not encapsulated, please refactor that later
    public void setDraggedCardSprite(CardSprite draggedSprite) {

        if(draggedSprite == null && this.draggedCardSprite != null){
            if (this.draggedCardSprite.collidesWith(deskSprite)) {
                if(this.cardOnTheTable != null){
                    this.cardOnTheTable.backToOriginalPosition(false);
                    resetZIndexes();
                    //mScene.sortChildren();
                }
                this.cardOnTheTable = this.draggedCardSprite;
                float movetoX = deskSprite.getX() + (deskSprite.getWidth() / 2)
                        - (this.draggedCardSprite.getWidth() / 2);
                float movetoY = deskSprite.getY() + (deskSprite.getHeight() / 4)
                        - (this.draggedCardSprite.getHeight() / 2);
                this.draggedCardSprite.registerEntityModifier(new MoveModifier(0.6f,
                        this.draggedCardSprite.getX(), movetoX, this.draggedCardSprite.getY(),
                        movetoY, EaseCubicInOut.getInstance()));
            }
        }
        else if(draggedSprite!=null && draggedSprite.equals(this.cardOnTheTable)){
            this.cardOnTheTable = null;
        }
        
        this.draggedCardSprite = draggedSprite;
    }

    public Engine getEngine() {
        return mEngine;
    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
                CardSprite.CARD_WIDTH, CardSprite.CARD_HEIGHT,
                TextureOptions.BILINEAR);

        this.mCardTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mBitmapTextureAtlas, this, "card_blank.png", 0, 0);
        this.mBitmapTextureAtlas.load();

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256,
                TextureOptions.BILINEAR);

        this.mDeskTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mBitmapTextureAtlas, this, "desk.png", 0, 0);
        this.mBitmapTextureAtlas.load();

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
                200, 200,
                TextureOptions.BILINEAR);
        this.tableTexture = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT,
                this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(),
                        "gfx/dark_texture.png"), this.getVertexBufferObjectManager());
        mBitmapTextureAtlas.load();

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
                128, 128,
                TextureOptions.BILINEAR);
        TextureRegion alignMenu = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mBitmapTextureAtlas, this, "view_refresh.png", 0, 0);
        this.osdMenu = new OSDMenu(this, this, alignMenu);
        this.mBitmapTextureAtlas.load();

        this.mFont = FontFactory.create(getFontManager(),
                getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 22);
        mFont.load();
        osdMenu.prepareTextures();

        SoundFactory.setAssetBasePath("mfx/");
        try {
            this.cardPickSound = SoundFactory.createSoundFromAsset(getEngine()
                    .getSoundManager(), this, "card_pick.wav");
            this.cardPutSound = SoundFactory.createSoundFromAsset(getEngine()
                    .getSoundManager(), this, "card_put.wav");
        } catch (final IOException e) {
            Debug.e(e);
        }

        CardSprite.setCardPickSound(cardPickSound);
        CardSprite.setCardPutSound(cardPutSound);
        CardSprite.setFont(mFont);

    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setOnAreaTouchTraversalFrontToBack();

        this.cards = DeckFactory.produce(DeckType.DEFAULT, mCardTextureRegion, this);
        for (CardSprite sprite : this.cards) {
            addCard(sprite);
        }

        this.osdMenu.prepareScene(mScene);
        deskSprite = new Sprite(CAMERA_WIDTH / 2 - 128, 0, mDeskTextureRegion,
                getVertexBufferObjectManager()) {

        };
        deskSprite.setY(CAMERA_HEIGHT / 2 - deskSprite.getHeight());
        this.mScene.attachChild(deskSprite);
        this.mScene.registerTouchArea(deskSprite);

        this.mScene.setBackground(this.tableTexture);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        mScene.registerUpdateHandler(sceneUpdateHandler);

        return this.mScene;
    }

    IUpdateHandler sceneUpdateHandler = new IUpdateHandler() {
        boolean colloding = false;

        private void setColliding(boolean colliding) {
            if (this.colloding != colliding) {
                if (colliding) {
                    deskSprite.registerEntityModifier(new ScaleModifier(0.5f, 1f, 1.5f,
                            EaseCubicInOut.getInstance()));
                }
                else {
                    deskSprite.registerEntityModifier(new ScaleModifier(0.5f, 1.5f, 1f,
                            EaseCubicInOut.getInstance()));
                }
            }
            this.colloding = colliding;
        }

        @Override
        public void reset() {
            setColliding(false);
        }

        @Override
        public void onUpdate(final float pSecondsElapsed) {
            if (draggedCardSprite == null)
                return;
            setColliding(draggedCardSprite.collidesWith(deskSprite));
        }
    };

    // ===========================================================
    // Methods
    // ===========================================================

    private List<CardSprite> cards;

    private void addCard(CardSprite sprite) {
        this.mScene.attachChild(sprite);
        this.mScene.registerTouchArea(sprite);
    }

    private void resetZIndexes(){
        for (CardSprite card : cards) {
            card.resetZIndex();
        }
        mScene.sortChildren();
    }
    
    public void clearCardsZIndex() {
        for (CardSprite card : cards) {
            card.setZIndex(0);
        }
    }

    @Override
    public void onAlignDeck() {
        for(CardSprite card : cards){
            card.backToOriginalPosition(false);
        }
        mScene.sortChildren();
        sceneUpdateHandler.reset();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
