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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This class contains the Heads-Up Display drawn on top of the gameScreen.
 *
 * TODO: DOCUMENTATION
 */
class HUD {
    private final GameScreen gameScreen;

    private Stage stage;
    private Viewport viewport;

    // Widgets for the HUD
    private Label throwAmountLabel, progressLabel;
    private TextButton skipButton;
    private TextButton pauseButton;

    // Used to track the things the HUD shows
    private int visitorsServed, visitorCount, throwAmount;
    private String throwAmountAsString, progressAsString;

    // Constructor creates all the widgets for now.
    // Note to self: clean this up -> separate things to their own methods.
    HUD(SpriteBatch batch, final KingsFeast kingsFeast, final GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        visitorsServed = this.gameScreen.getVISITORS_SERVED();
        visitorCount = this.gameScreen.getLevelData().getVisitorCount();
        throwAmount = this.gameScreen.getTHROW_AMOUNT();

        throwAmountAsString = "Throws: " + throwAmount;
        progressAsString = "Progress: " + visitorsServed + " / " + visitorCount;

        viewport = new FitViewport(Util.convertMetresToPixels(gameScreen.getGAME_WIDTH()),
                Util.convertMetresToPixels(gameScreen.getGAME_HEIGHT()),
                new OrthographicCamera());

        stage = new Stage(viewport, batch);

        throwAmountLabel = new Label(throwAmountAsString,
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        progressLabel = new Label (progressAsString,
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        Texture skipButtonDown = kingsFeast.getAssetManager().get("skipButton-down.png");
        Texture skipButtonUp = kingsFeast.getAssetManager().get("skipButton-up.png");
        skipButton = new TextButton("Skip", new TextButton.TextButtonStyle(
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                new TextureRegionDrawable(new TextureRegion(skipButtonDown)),
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                new BitmapFont()));

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
        pauseButton = new TextButton("Pause", new TextButton.TextButtonStyle(
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                new TextureRegionDrawable(new TextureRegion(skipButtonDown)),
                new TextureRegionDrawable(new TextureRegion(skipButtonUp)),
                new BitmapFont()));

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

        stage.addActor(table);
    }

    void dispose() {
        stage.dispose();
    }

    void update() {
        visitorCount = this.gameScreen.getLevelData().getVisitorCount();
        visitorsServed = this.gameScreen.getVISITORS_SERVED();
        throwAmount = this.gameScreen.getTHROW_AMOUNT();

        throwAmountAsString = "Throws: " + throwAmount;
        progressAsString = "Progress: " + visitorsServed + " / " + visitorCount;

        progressLabel.setText(progressAsString);
        throwAmountLabel.setText(throwAmountAsString);
    }

    Stage getStage() {
        return stage;
    }
}
