package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// this needs elaboration
/**
 * This class contains the Heads-Up Display drawn on top of the GameScreen.
 *
 * Made utilizing scene2d stages and tables. Has its own camera so it stays on the screen.
 *
 */
class HUD {
    private final KingsFeast kingsFeast;
    private final GameScreen gameScreen;

    private Stage stage;
    private I18NBundle myBundle;

    // Widgets for the HUD
    private Label throwAmountLabel, progressLabel, recentlyScoredLabel;
    private TextButton skipButton, pauseButton;

    // Used to track the things the HUD shows
    private int visitorsServed, visitorCount, throwAmount;
    private String throwAmountAsString, progressAsString;

    /**
     * Creates a head-up display which is overlaid on game screen.
     *
     * Has its own camera, so that it stays on the screen. Head-up display buttons, skip button
     * and pause button, are created. Labels to show throws and progress are also created. These
     * widgets are then added to a scene2d table so they can be aligned easily.
     *
     * @param batch Takes in the spriteBatch for drawing purposes
     * @param kingsFeast Game object so its and its parent's methods can be accessed.
     * @param gameScreen GameScreen object to get relevant data for the HUD from.
     */
    HUD(SpriteBatch batch, final KingsFeast kingsFeast, final GameScreen gameScreen) {
        this.kingsFeast = kingsFeast;
        this.gameScreen = gameScreen;
        visitorsServed = this.gameScreen.getVISITORS_SERVED();
        visitorCount = this.gameScreen.getLevelData().getVisitorCount();
        throwAmount = this.gameScreen.getTHROW_AMOUNT();

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();

        Viewport viewport = new FitViewport(Util.convertMetresToPixels(gameScreen.getGAME_WIDTH()),
                Util.convertMetresToPixels(gameScreen.getGAME_HEIGHT()),
                new OrthographicCamera());

        stage = new Stage(viewport, batch);

        createButtonsAndLabels();
        createTable();
    }

    /**
     * Gets rid of the stage of this class.
     * Called in GameScreen's dispose() method.
     */
    void dispose() {
        stage.dispose();
    }

    /**
     * Makes all information shown in the HUD update in real time.
     * This is called in the render method of GameScreen.
     */
    void update() {
        visitorCount = this.gameScreen.getLevelData().getVisitorCount();
        visitorsServed = this.gameScreen.getVISITORS_SERVED();
        throwAmount = this.gameScreen.getTHROW_AMOUNT();

        skipButton.setText(myBundle.get("skip"));
        pauseButton.setText(myBundle.get("pause"));

        throwAmountAsString = myBundle.get("throwes")+ ": " + throwAmount;
        progressAsString = myBundle.get("progress")+ ": " + visitorsServed + " / " + visitorCount;

        progressLabel.setText(progressAsString);
        throwAmountLabel.setText(throwAmountAsString);
        if(gameScreen.getFoodPlate().recentlyScored) {
            recentlyScoredLabel.setText(myBundle.get("recentlyScoredLabel"));
        } else {
            recentlyScoredLabel.setText("");
        }
    }

    /**
     * Buttons and their functionality are done here. Same for labels.
     *
     * Skip button ends an ongoing throw early.
     * Pause button shows the pause screen.
     * Labels are used to show throws and progress in a level.
     */
    private void createButtonsAndLabels() {
        Texture skipButtonDown = kingsFeast.getAssetManager().get("tyhjanappi.png");
        Texture skipButtonUp = kingsFeast.getAssetManager().get("tyhjanappi.png");
        int FONT_SIZE = 28;
        BitmapFont bitmapFont = Util.initFont(FONT_SIZE);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                new TextureRegionDrawable(new TextureRegion(skipButtonDown)),
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                bitmapFont);

        throwAmountAsString = myBundle.get("throwes")+ ": " + throwAmount;
        progressAsString = myBundle.get("progress")+ ": " + visitorsServed + " / " + visitorCount;

        // Labels
        Label.LabelStyle labelStyle = new Label.LabelStyle(bitmapFont, Color.WHITE);
        throwAmountLabel = new Label(throwAmountAsString, labelStyle);
        progressLabel = new Label (progressAsString, labelStyle);
        recentlyScoredLabel = new Label ("", labelStyle);

        // Skip Button
        skipButton = new TextButton(myBundle.get("skip"),  textButtonStyle);
        skipButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                System.out.println(x + " + " + y);
                if (gameScreen.getFoodPlate().isPlateFlying) {
                    gameScreen.getFoodPlate().isPlateFlying = false;
                    gameScreen.getFoodPlate().removeBody= true;
                }
            }
        });

        // Pause Button
        pauseButton = new TextButton(myBundle.get("pause"), textButtonStyle);
        pauseButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                kingsFeast.setScreen(new PauseScreen(kingsFeast, gameScreen.getThis()));
            }
        });

    }

    /**
     * Creates a table and adds all the widgets to it. Also aligns them how intended. Finally
     * adds the table to the stage.
     */
    private void createTable() {
        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        table.add(throwAmountLabel).expandX().left().padLeft(50).padTop(10);
        table.add(progressLabel).expandX().right().padRight(50).padTop(10);
        table.row();
        table.add(skipButton).expandX().left().padLeft(50).padTop(10);
        table.add(pauseButton).expandX().right().padRight(50).padTop(10);
        table.row();
        table.add(recentlyScoredLabel).colspan(3).center().padTop(100);

        stage.addActor(table);
    }

    /**
     * Simply updates the bundle in case language has been changed.
     * After returning from the pause menu, GameScreen calls this to make sure correct
     * language is in use.
     */
    void updateI18NBundle() {
        this.myBundle = kingsFeast.langManager.getCurrentBundle();
    }

    Stage getStage() {
        return stage;
    }
}
