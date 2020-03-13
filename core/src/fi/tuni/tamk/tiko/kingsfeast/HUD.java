package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This class contains the Heads-Up Display drawn on top of the gameScreen.
 *
 * TODO: DOCUMENTATION
 */
public class HUD {

    private final GameScreen gameScreen;

    private Stage stage;
    private Viewport viewport;

    private Label throwAmountLabel, progressLabel;

    private int visitorsServed, visitorCount, throwAmount;
    private String throwAmountAsString, progressAsString;

    HUD(SpriteBatch batch, GameScreen gameScreen) {
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

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        table.add(throwAmountLabel).expandX().left().padLeft(50).padTop(10);
        table.add(progressLabel).expandX().right().padRight(50).padTop(10);

        stage.addActor(table);
    }

    public void dispose() {
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
