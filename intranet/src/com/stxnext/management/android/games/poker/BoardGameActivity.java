
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

import android.graphics.Point;
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
    // private BitmapTextureAtlas mCardDeckTexture;
    private Scene mScene;
    // private HashMap<Card, ITextureRegion> mCardTotextureRegionMap;
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mCardTextureRegion;
    private TextureRegion mDeskTextureRegion;
    private Sprite deskSprite;
    private Font mFont;
    private Sound cardPickSound;
    private Sound cardPutSound;
    private RepeatingSpriteBackground tableTexture;
    private OSDMenu osdMenu;
    private CardSprite activeCardSprite;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        CAMERA_WIDTH = size.x;
        CAMERA_HEIGHT = size.y;

        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(true,
                ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH,
                        CAMERA_HEIGHT), this.mCamera);
        engineOptions.getRenderOptions().setMultiSampling(true);
        engineOptions.getAudioOptions().setNeedsSound(true);
        engineOptions.getRenderOptions().setDithering(true);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);

        return engineOptions;
    }

    CardSprite previouslyActiveCardSprite;

    // kind of complicated and not encapsulated, please refactor that later
    public void setActiveCardSprite(CardSprite activeCardSprite) {

        if (activeCardSprite == null && this.activeCardSprite != null) {

            if (this.activeCardSprite.collidesWith(deskSprite)) {
                
                if (previouslyActiveCardSprite != null
                        && previouslyActiveCardSprite.collidesWith(deskSprite)) {
                    previouslyActiveCardSprite.backToOrigilanPosition(cards
                            .indexOf(previouslyActiveCardSprite));
                }
                
                float movetoX = deskSprite.getX() + (deskSprite.getWidth() / 2)
                        - (this.activeCardSprite.getWidth() / 2);
                float movetoY = deskSprite.getY() + (deskSprite.getHeight() / 4)
                        - (this.activeCardSprite.getHeight() / 2);
                this.activeCardSprite.registerEntityModifier(new MoveModifier(0.6f,
                        this.activeCardSprite.getX(), movetoX, this.activeCardSprite.getY(),
                        movetoY, EaseCubicInOut.getInstance()));
            }
            previouslyActiveCardSprite = this.activeCardSprite;
        }

        // if(activeCardSprite!=null){
        // float movetoX =
        // deskSprite.getX()+(deskSprite.getWidth()/2)-(activeCardSprite.getWidth()/2);
        // float movetoY =
        // deskSprite.getY()+(deskSprite.getHeight()/4)-(activeCardSprite.getHeight()/2);
        // activeCardSprite.registerEntityModifier(new MoveModifier(0.6f,
        // activeCardSprite.getX(),movetoX , activeCardSprite.getY(), movetoY,
        // EaseCubicInOut.getInstance()));
        // }

        this.activeCardSprite = activeCardSprite;
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
        // this.mScene.setScale(CardSprite.cardGlobalScale);

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

        // new Background(0.09804f, 0.6274f, 0.8784f)
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
            if (activeCardSprite == null)
                return;
            setColliding(activeCardSprite.collidesWith(deskSprite));
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

    public void clearCardsZIndex() {
        for (CardSprite card : cards) {
            card.setZIndex(0);
        }
    }

    @Override
    public void onAlignDeck() {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).backToOrigilanPosition(i);
        }
        mScene.sortChildren();
        sceneUpdateHandler.reset();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
