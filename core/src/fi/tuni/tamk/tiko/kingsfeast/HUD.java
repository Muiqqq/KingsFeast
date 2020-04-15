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

/**
 * This class contains the Heads-Up Display drawn on top of the gameScreen.
 *
 * TODO: DOCUMENTATION
 */
class HUD {
    private final KingsFeast kingsFeast;
    private final GameScreen gameScreen;

    private Stage stage;
    private Viewport viewport;
    private BitmapFont bitmapFont;
    private TextButton.TextButtonStyle textButtonStyle;
    private int FONT_SIZE;
    I18NBundle myBundle;

    // Widgets for the HUD
    private Label throwAmountLabel, progressLabel, recentlyScoredLabel;
    private TextButton skipButton;
    private TextButton pauseButton;

    // Used to track the things the HUD shows
    private int visitorsServed, visitorCount, throwAmount;
    private String throwAmountAsString, progressAsString;

    // Constructor creates all the widgets for now.
    // Note to self: clean this up -> separate things to their own methods.
    HUD(SpriteBatch batch, final KingsFeast kingsFeast, final GameScreen gameScreen) {
        this.kingsFeast = kingsFeast;
        this.gameScreen = gameScreen;
        visitorsServed = this.gameScreen.getVISITORS_SERVED();
        visitorCount = this.gameScreen.getLevelData().getVisitorCount();
        throwAmount = this.gameScreen.getTHROW_AMOUNT();

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();

        Texture skipButtonDown = kingsFeast.getAssetManager().get("tyhjanappi.png");
        Texture skipButtonUp = kingsFeast.getAssetManager().get("tyhjanappi.png");
        FONT_SIZE = 28;
        bitmapFont = Util.initFont(FONT_SIZE);
        textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                new TextureRegionDrawable(new TextureRegion(skipButtonDown)),
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                bitmapFont);

        throwAmountAsString = myBundle.get("throwes")+ ": " + throwAmount;
        progressAsString = myBundle.get("progress")+ ": " + visitorsServed + " / " + visitorCount;

        viewport = new FitViewport(Util.convertMetresToPixels(gameScreen.getGAME_WIDTH()),
                Util.convertMetresToPixels(gameScreen.getGAME_HEIGHT()),
                new OrthographicCamera());

        stage = new Stage(viewport, batch);

        Label.LabelStyle labelStyle = new Label.LabelStyle(bitmapFont, Color.WHITE);
        throwAmountLabel = new Label(throwAmountAsString, labelStyle);
        progressLabel = new Label (progressAsString, labelStyle);
        recentlyScoredLabel = new Label ("", labelStyle);

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

        // Table stores all the scene2d.ui widgets
        // Here we add the widgets to the table and then add the table to the stage
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

    void dispose() {
        stage.dispose();
    }

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

    void updateI18NBundle() {
        this.myBundle = kingsFeast.langManager.getCurrentBundle();
    }

    Stage getStage() {
        return stage;
    }
}
