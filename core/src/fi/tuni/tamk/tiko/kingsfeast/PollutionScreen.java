package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PollutionScreen extends ScreenAdapter {
    private final Game game;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private Texture backgroundTexture;
    private Stage stage;

    // DOCUMENTATION

    public PollutionScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createBackgroundImage());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    private Image createBackgroundImage() {
        backgroundTexture = new Texture("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
