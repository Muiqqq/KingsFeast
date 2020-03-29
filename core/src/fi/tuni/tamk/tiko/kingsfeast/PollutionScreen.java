package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PollutionScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private Texture backgroundTexture;
    private Stage stage;
    private Texture okTexture;

    // DOCUMENTATION

    public PollutionScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createBackgroundImage());
        stage.addActor(createOkButton());
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

    private ImageButton createOkButton() {
        okTexture = new Texture("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setPosition(GAME_WIDTH - 310, GAME_HEIGHT / 4);
        ok.setSize(150f, 75f);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new GameScreen(kingsFeast));
                dispose();
            }
        });
        return ok;
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
