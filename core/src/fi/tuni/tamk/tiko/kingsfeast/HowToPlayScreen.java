package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class HowToPlayScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private final GameScreen gameScreen;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private Stage stage;
    private final float BUTTON_WIDTH = 550f;
    private final float BUTTON_HEIGHT = 150f;

    // Textures
    private Texture backgroundTexture;

    public HowToPlayScreen(KingsFeast kingsFeast, GameScreen gameScreen) {
        this.kingsFeast = kingsFeast;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons to the stage
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

    // Returns background image
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
