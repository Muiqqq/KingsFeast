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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Quick mockup to test different screens. Will eventually become the actual main menu.
 */
public class MainMenuScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    // Placeholder values
    private final float BUTTON_WIDTH = 300f;
    private final float BUTTON_HEIGHT = 83f;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture playUnpressedTexture;
    private Texture playPressedTexture;
    private Texture settingsTexture;
    private Texture newGameTexture;

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
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // adds a background img and play button to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createContinueButton());
        stage.addActor(createSettingsButton());
        stage.addActor(createHowToPlayButton());
        stage.addActor(createNewGameButton());
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
    }

    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("mainmenubackgroundtitle.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createContinueButton() {
        // two textures are used to give the user some feedback when pressing a button
        playUnpressedTexture = kingsFeast.getAssetManager().get("ContinueButton.png");

        // this line is way too goddamn long
        ImageButton continueGame =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(playUnpressedTexture)));

        continueGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        continueGame.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH * 2, GAME_HEIGHT / 8);

        // button's functionality
        continueGame.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                kingsFeast.setScreen(new GameScreen(kingsFeast));
                dispose();
            }
        });

        return continueGame;
    }

    private ImageButton createSettingsButton() {
        settingsTexture = kingsFeast.getAssetManager().get("SettingsButton.png");
        ImageButton settingsButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)));
        settingsButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 8);
        settingsButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                kingsFeast.setScreen(new OptionsScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });
        return settingsButton;
    }

    private ImageButton createHowToPlayButton() {
        settingsTexture = kingsFeast.getAssetManager().get("HowToPlayButton.png");
        ImageButton howToPlay =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)));
        howToPlay.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        howToPlay.setPosition(GAME_WIDTH / 2 + BUTTON_WIDTH , GAME_HEIGHT / 8);
        howToPlay.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                kingsFeast.setScreen(new OptionsScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });
        return howToPlay;
    }

    private ImageButton createNewGameButton() {
            newGameTexture = kingsFeast.getAssetManager().get("NewGameButton.png");
            ImageButton newGame =
                    new ImageButton(new TextureRegionDrawable(new TextureRegion(newGameTexture)));
                    newGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
                    newGame.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH , GAME_HEIGHT / 8);
                    newGame.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent e, float x, float y, int count, int button) {
                    super.tap(e, x, y, count, button);
                    kingsFeast.setScreen(new GameScreen(kingsFeast));
                    dispose();
                }
            });
            return newGame;
        }

    private MainMenuScreen getThisScreen() {
        return this;
    }
}
