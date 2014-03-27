
package com.stxnext.management.android.games.poker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicInOut;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.games.poker.GameDashboardFragment.GameDashboardListener;
import com.stxnext.management.android.games.poker.OSDMenu.OSDMenuListener;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler.NIOConnectionNotificationHandlerCallbacks;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler.NIOConnectionRequestHandlerCallbacks;
import com.stxnext.management.android.ui.dependencies.ExtendedViewPager;
import com.stxnext.management.android.ui.dependencies.SimplePlayersGridAdapter;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;
import com.stxnext.management.server.planningpoker.server.dto.messaging.MessageWrapper;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.RequestFor;
import com.stxnext.management.server.planningpoker.server.dto.messaging.in.SessionMessage;
import com.stxnext.management.server.planningpoker.server.dto.messaging.out.DeckSetMessage;

public class BoardGameActivity extends SimpleBaseGameActivity implements OSDMenuListener,
        NIOConnectionNotificationHandlerCallbacks, NIOConnectionRequestHandlerCallbacks, GameDashboardListener {
    public static int CAMERA_HEIGHT = 720;
    public static int CAMERA_WIDTH = 480;

    private Camera mCamera;
    private Scene mScene;
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mCardTextureRegion;
    private TextureRegion mDeskTextureRegion;
    private TextureRegion mHourGlassTextureRegion;
    private Sprite deskSprite;
    private Sprite hourGlassSprite;
    private Font mFont;
    private Text subjectText;
    private Sound cardPickSound;
    private Sound cardPutSound;
    private RepeatingSpriteBackground backgroundTexture;
    private OSDMenu osdMenu;
    private CardSprite draggedCardSprite;
    private CardSprite cardOnTheTable;
    ProgressDialog progressDialog;
    SimpleFragmentAdapter fragmentAdapter;
    ExtendedViewPager viewPager;

    // main native views
    private LinearLayout rootView;

    private NIOConnectionHandler nioHandler;
    private GameData gameData;
    VoteResultFragment voteResultFragment;
    GameDashboardFragment gameDashBoardFragment;
    

    @Override
    protected synchronized void onResume() {
        nioHandler.addNotificationListener(this);
        nioHandler.addRequestListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        nioHandler.removeNotificationListener(this);
        nioHandler.removeRequestListener(this);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        nioHandler = NIOConnectionHandler.getInstance();
        gameData = GameData.getInstance();

        
        
        gameDashBoardFragment = new GameDashboardFragment(this);
        voteResultFragment = new VoteResultFragment();
        fragmentAdapter = new SimpleFragmentAdapter(this, getSupportFragmentManager());
        fragmentAdapter.addFragment(gameDashBoardFragment);
        fragmentAdapter.addFragment(voteResultFragment);
        viewPager = (ExtendedViewPager) rootView.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setPagingEnabled(false);

        nioHandler.enqueueRequest(RequestFor.PlayersInLiveSession,
                gameData.getSessionMessageInstance(null));
    }

    private void onGamePrepared() {
        updateSceneType(SceneType.MasterPreparingTicket);
        ;
    }

    @Override
    protected void onSetContentView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        rootView = (LinearLayout) inflater.inflate(R.layout.game_main, null);
        
        LinearLayout gameContainer = (LinearLayout) rootView.findViewById(R.id.gameContainer);
        final LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 1f);
        rootLayoutParams.setLayoutDirection(LinearLayout.VERTICAL);
        final LinearLayout.LayoutParams surfaceViewLayoutParams = new LinearLayout.LayoutParams(
                super.createSurfaceViewLayoutParams());

        this.mRenderSurfaceView = new RenderSurfaceView(this);
        this.mRenderSurfaceView.setRenderer(this.mEngine, this);

        gameContainer.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);

        this.setContentView(rootView, rootLayoutParams);
    }

    @SuppressWarnings("deprecation")
    // need to support gingerbread
    @Override
    public EngineOptions onCreateEngineOptions() {

        TypedValue value = new TypedValue();
        value.type = TypedValue.TYPE_FLOAT;
        getResources().getValue(R.dimen.game_surface_heigth, value, false);
        float heigthScale = value.getFloat();// getResources().getDimension(R.dimen.game_surface_heigth);
        Display display = getWindowManager().getDefaultDisplay();
        CAMERA_WIDTH = display.getWidth();
        CAMERA_HEIGHT = (int) (display.getHeight() * heigthScale);

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

        if (draggedSprite == null && this.draggedCardSprite != null) {
            if (this.draggedCardSprite.collidesWith(deskSprite)) {
                if (this.cardOnTheTable != null) {
                    this.cardOnTheTable.backToOriginalPosition(false);
                    resetZIndexes();
                    // mScene.sortChildren();
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
        else if (draggedSprite != null && draggedSprite.equals(this.cardOnTheTable)) {
            this.cardOnTheTable = null;
        }

        this.draggedCardSprite = draggedSprite;
    }

    public Engine getEngine() {
        return mEngine;
    }

    @Override
    public void setSubjectText(String text) {
        if (text == null)
            text = "";

        if (subjectText != null) {
            mScene.detachChild(subjectText);
        }
        subjectText = new Text(0, deskSprite.getY() + deskSprite.getHeight()
                + 50, mFont, text, new TextOptions(
                HorizontalAlign.CENTER), getVertexBufferObjectManager());
        mScene.attachChild(subjectText);
        subjectText.setX(CAMERA_WIDTH / 2 - (subjectText.getWidth() / 2));
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()>0){
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
            return;
        }
        
        String title = gameData.amiGameMaster() ? "Finish session" : "Leave session";
        String content = gameData.amiGameMaster() ? "Do you want to finish that session? It will also drop other players from it."
                : "Do you really want to flee from this gathering ;) ?";

        Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setNegativeButton(getString(R.string.common_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setPositiveButton(getString(R.string.common_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                nioHandler.stop();
                                finish();
                            }
                        });
        builder.show();
    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mCardTextureRegion = loadTexture(CardSprite.CARD_WIDTH, CardSprite.CARD_HEIGHT,
                "card_blank.png");
        this.mDeskTextureRegion = loadTexture(256, 256, "desk.png");
        this.mHourGlassTextureRegion = loadTexture(128, 128, "hourglass.png");
        // this.backgroundTexture = loadTexture(width, height, fileName)
        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
                10, 10);
        this.backgroundTexture = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT,
                this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(),
                        "gfx/green_texture.png"), this.getVertexBufferObjectManager());
        mBitmapTextureAtlas.load();

        TextureRegion alignMenuIcon = loadTexture(128, 128, "view_refresh.png");
        this.osdMenu = new OSDMenu(this, this, alignMenuIcon);

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

    private TextureRegion loadTexture(int width, int height, String fileName) {
        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
                width, height);
        TextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mBitmapTextureAtlas, this, fileName, 0, 0);
        this.mBitmapTextureAtlas.load();
        return textureRegion;
    }

    final LoopEntityModifier hourglassModifier = new LoopEntityModifier(new RotationModifier(2, 0f,
            360f, EaseCubicInOut.getInstance()));

    private void setLoading(boolean loading) {
        hourGlassSprite.setVisible(loading);
        hourGlassSprite.unregisterEntityModifier(hourglassModifier);
        if (loading) {
            hourGlassSprite.registerEntityModifier(hourglassModifier);
        }
    }

    private void updateSceneType(SceneType type) {
        switch (type) {
            case NewTicketSet:
                if (gameData.amiGameMaster()) {
                    setLoading(true);
                    setSubjectText("Waiting for players votes on "
                            + gameData.getTicketBeingConsidered().getDisplayValue() + "...");
                    gameDashBoardFragment.setRevealAreaVisible(true);
                    gameDashBoardFragment.setSubmitTicketAreaVisible(false);
                    //revealArea.setVisibility(View.VISIBLE);
                    //submitTicketArea.setVisibility(View.GONE);

                }
                else {
                    gameDashBoardFragment.setVotingAnabled(true);
                    setSubjectText("Voting for " + gameData.ticketBeingConsidered.getDisplayValue());
                    prepareVotingScene();
                }
                break;
            case WaitingForVoting:
                if (!gameData.amiGameMaster()) {
                    hideAllOnScene();
                    setLoading(true);
                    setSubjectText("Waiting for other players votes on "
                            + gameData.getTicketBeingConsidered().getDisplayValue() + "...");
                    gameDashBoardFragment.setVotingAnabled(false);
                    //voteButton.setEnabled(false);
                    cardOnTheTable.setVisible(true);
                    cardOnTheTable.setX(CAMERA_WIDTH / 2 - cardOnTheTable.getWidth() / 2);
                    cardOnTheTable.setY(CAMERA_HEIGHT / 2 - cardOnTheTable.getHeight() / 2);
                    cardOnTheTable.registerEntityModifier(new ScaleModifier(1.5f, 1f, 4f,
                            EaseCubicInOut.getInstance()));
                }
                break;
            case VotesRevealed:
                gameDashBoardFragment.getPlayerGridAdapter().clearVotes();
                hideAllOnScene();
                voteResultFragment.setVotes(gameData.getTicketBeingConsidered().getVotes());
                viewPager.setCurrentItem(1, true);
                gameDashBoardFragment.setRevealAreaVisible(false);
                gameDashBoardFragment.setSubmitTicketAreaVisible(true);
                break;

            case MasterDisconnected:

                break;
            case MasterPreparingTicket:
                if (gameData.amiGameMaster()) {
                    hideAllOnScene();
                    setLoading(true);
                    setSubjectText("Add new ticket");
                    gameDashBoardFragment.setRevealAreaVisible(false);
                    gameDashBoardFragment.setSubmitTicketAreaVisible(true);
//                    revealArea.setVisibility(View.GONE);
//                    submitTicketArea.setVisibility(View.VISIBLE);
                }
                else {
                    hideAllOnScene();
                    setSubjectText("Waiting for new ticket");
                    setLoading(true);
                }
                break;
        }
    }

    private void hideAllOnScene() {
        this.deskSprite.setVisible(false);
        this.osdMenu.setVisible(false);
        for (CardSprite card : cards) {
            card.setVisible(false);
        }
    }

    private void prepareVotingScene() {
        for (CardSprite card : cards) {
            card.setVisible(true);
        }
        this.osdMenu.setVisible(true);
        this.deskSprite.setVisible(true);
        setLoading(false);
        onAlignDeck();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        mScene.setBackground(new Background(0.45f, 0.77f, 0.72f, 1f));
        this.mScene.setOnAreaTouchTraversalFrontToBack();

        this.cards = DeckFactory
                .produce(gameData.getCurrentSessionDeck(), mCardTextureRegion, this);
        for (CardSprite sprite : this.cards) {
            addCard(sprite);
        }

        this.osdMenu.prepareScene(mScene);
        deskSprite = new Sprite(CAMERA_WIDTH / 2 - 128, 0, mDeskTextureRegion,
                getVertexBufferObjectManager()) {

        };

        hourGlassSprite = new Sprite(CAMERA_WIDTH / 2 - (mHourGlassTextureRegion.getWidth() / 2),
                50, mHourGlassTextureRegion, getVertexBufferObjectManager());
        this.mScene.attachChild(hourGlassSprite);
        this.mScene.attachChild(deskSprite);
        this.mScene.registerTouchArea(deskSprite);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        mScene.registerUpdateHandler(sceneUpdateHandler);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onGamePrepared();
            }
        });

        return this.mScene;
    }

    IUpdateHandler sceneUpdateHandler = new IUpdateHandler() {
        boolean colloding = false;

        private void setColliding(boolean colliding) {
            if (this.colloding != colliding) {
                if (colliding) {
                    deskSprite.registerEntityModifier(new ScaleModifier(0.4f, 1f, 1.3f,
                            EaseCubicInOut.getInstance()));
                }
                else {
                    deskSprite.registerEntityModifier(new ScaleModifier(0.4f, 1.3f, 1f,
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

    private List<CardSprite> cards;

    private void addCard(CardSprite sprite) {
        this.mScene.attachChild(sprite);
        this.mScene.registerTouchArea(sprite);
    }

    private void resetZIndexes() {
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
        for (CardSprite card : cards) {
            card.backToOriginalPosition(false);
        }
        mScene.sortChildren();
        sceneUpdateHandler.reset();
    }

    private enum SceneType {
        NewTicketSet,
        WaitingForVoting,
        VotesRevealed,
        MasterDisconnected,
        MasterPreparingTicket;
    }

    @Override
    public void onDecksReceived(MessageWrapper<DeckSetMessage> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreateSessionReceived(MessageWrapper<Session> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerSessionReceived(MessageWrapper<List<Session>> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayersCreateReceived(MessageWrapper<List<Player>> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLivePlayersReceived(MessageWrapper<List<Player>> msg) {
        gameData.setLivePlayers(msg.getPayload());
        gameDashBoardFragment.getPlayerGridAdapter().setList(gameData.getLivePlayers());
    }

    @Override
    public void onJoinSessionReceived(MessageWrapper<Player> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUserConnectionStateChanged(MessageWrapper<SessionMessage<Player>> msg) {
        Player player = msg.getPayload().getSessionSubject();
        gameData.manageLivePlayer(player);
        gameDashBoardFragment.getPlayerGridAdapter().setList(gameData.getLivePlayers());
        if (player.equals(gameData.sessionIamIn.getOwner()) && !gameData.amiGameMaster()) {
            if (!player.isActive()) {
                progressDialog = new ProgressDialog(BoardGameActivity.this);
                progressDialog.setTitle("Game master disconnected!");
                progressDialog.setMessage("Please wait until game master reconnects...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
            else if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onNewTicketRoundReceived(MessageWrapper<SessionMessage<Ticket>> msg) {
        gameData.setTicketBeingConsidered(msg.getPayload().getSessionSubject());
        updateSceneType(SceneType.NewTicketSet);
    }

    @Override
    public void onVoteReceived(MessageWrapper<SessionMessage<Vote>> msg) {
        Player player = msg.getPayload().getSessionSubject().getPlayer();
        gameDashBoardFragment.getPlayerGridAdapter().setPlayerVoted(player);
        if (gameData.getCurrentHandshakenPlayer().equals(player)) {
            updateSceneType(SceneType.WaitingForVoting);
        }
        // gameData.setTicketBeingConsidered(msg.getPayload().getSessionSubject());
    }

    @Override
    public void onRevealVotesReceived(MessageWrapper<SessionMessage<Ticket>> msg) {
        gameData.setTicketBeingConsidered(msg.getPayload().getSessionSubject());
        updateSceneType(SceneType.VotesRevealed);
        setLoading(false);
    }

    @Override
    public void onFinishSessionReceived(MessageWrapper<SessionMessage<Session>> msg) {
        // TODO Auto-generated method stub

    }


    @Override
    public CardSprite getCardOnTheTable() {
        return cardOnTheTable;
    }
}
