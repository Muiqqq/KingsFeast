package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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
    private final float BUTTON_WIDTH = 115.17f;
    private final float BUTTON_HEIGHT = 31.84f;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture playUnpressedTexture;
    private Texture playPressedTexture;
    private Texture settingsTexture;

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
        stage.addActor(createResetButton());
        stage.addActor(createSettingsButton());
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
        playUnpressedTexture = new Texture("StartGameButton.png");
        playPressedTexture = new Texture("StartGameButton.png");

        // this line is way too goddamn long
        ImageButton playButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(playUnpressedTexture)),
                        new TextureRegionDrawable(new TextureRegion(playPressedTexture)));

        playButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        playButton.setPosition(GAME_WIDTH / 2,
                GAME_HEIGHT / 3 + BUTTON_HEIGHT + 10,
                Align.center);

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

    private ImageButton createResetButton() {
        // two textures are used to give the user some feedback when pressing a button
        playUnpressedTexture = new Texture("ResetSaveButtonPlaceholder.png");
        playPressedTexture = new Texture("ResetSaveButtonPlaceholder.png");

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

                Dialog dialog = new Dialog("",
                        new Window.WindowStyle(new BitmapFont(), Color.WHITE, null)) {

                    public void result(Object obj) {
                        if ((boolean) obj) {
                            kingsFeast.clearSaveState();
                            kingsFeast.setCurrentLevel
                                    (kingsFeast.getPrefs().getInteger("currentLevel"));
                        }
                    }
                };

                Texture buttonDown = kingsFeast.getAssetManager().get("skipButton-down.png");
                Texture buttonUp = kingsFeast.getAssetManager().get("skipButton-up.png");

                TextButton yesButton = new TextButton("Yes", new TextButton.TextButtonStyle(
                        new TextureRegionDrawable(new TextureRegion(buttonUp)),
                        new TextureRegionDrawable(new TextureRegion(buttonDown)),
                        new TextureRegionDrawable(new TextureRegion(buttonUp)),
                        new BitmapFont()));

                TextButton noButton = new TextButton("No", new TextButton.TextButtonStyle(
                        new TextureRegionDrawable(new TextureRegion(buttonUp)),
                        new TextureRegionDrawable(new TextureRegion(buttonDown)),
                        new TextureRegionDrawable(new TextureRegion(buttonUp)),
                        new BitmapFont()));

                dialog.text(new Label("Do you really want to reset save data?",
                        new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
                dialog.button(yesButton, true);
                dialog.button(noButton, false);
                dialog.show(stage);

            }
        });

        return playButton;
    }

    private ImageButton createSettingsButton() {
        settingsTexture = new Texture("SettingsButton.png");
        ImageButton settingsButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)));
        settingsButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 4, Align.center);
        settingsButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                kingsFeast.setScreen(new OptionsScreen(kingsFeast));
                dispose();
            }
        });
        return settingsButton;
    }
}
