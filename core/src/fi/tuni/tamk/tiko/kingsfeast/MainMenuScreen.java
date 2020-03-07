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
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Quick mockup to test different screens. Will eventually become the actual main menu.
 */
public class MainMenuScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    // Placeholder values
    private final float BUTTON_WIDTH = 128f;
    private final float BUTTON_HEIGHT = 96f;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture playUnpressedTexture;
    private Texture playPressedTexture;

    // Constructor here takes the game object so we can swap to a different screen from this one.
    MainMenuScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    // screens use show() instead of create()
    // same thing essentially.
    @Override
    public void show() {

        // create the stage with a viewport.
        // set stage as the inputProcessor for this screen
        // so the stage can handle it, just gotta remember to
        // add a listener to every button.
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // adds a background img and play button to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createPlayButton());
    }

    // screen flickers when resizing during runtime,
    // might be worth checking other viewport options.

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // stage.act() calls every single actor's act() method.
    // stage.draw() draws all actors.
    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        playPressedTexture.dispose();
        playUnpressedTexture.dispose();
    }

    private Image createBackgroundImage() {
        backgroundTexture = new Texture("MainMenuBackgroundPlaceholder.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createPlayButton() {
        // two textures are used to give the user some feedback when pressing a button
        playUnpressedTexture = new Texture("unpressedPlayButtonPlaceholder.png");
        playPressedTexture = new Texture("pressedPlayButtonPlaceholder.png");

        // this line is way too goddamn long
        ImageButton playButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(playUnpressedTexture)),
                        new TextureRegionDrawable(new TextureRegion(playPressedTexture)));

        playButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        playButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 3, Align.center);

        // button's functionality
        playButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                kingsFeast.setScreen(new GameScreen(kingsFeast));
                dispose();
            }
        });

        return playButton;
    }
}
