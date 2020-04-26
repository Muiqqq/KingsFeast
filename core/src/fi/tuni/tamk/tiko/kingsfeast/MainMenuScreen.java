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
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * MainMenuScreen class handles all data concerning Main Menu and its creation.
 */
public class MainMenuScreen extends ScreenAdapter {
    // Game object
    private final KingsFeast kingsFeast;

    // Screen stuff
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private final float BUTTON_WIDTH = 400f;
    private final float BUTTON_HEIGHT = 90f;
    private Stage stage;

    // Textures
    private Texture backgroundTexture;
    private Texture playUnpressedTexture;
    private Texture settingsTexture;
    private Texture newGameTexture;

    // Localization
    private I18NBundle myBundle;

    /**
     * Constructor takes the game object to access its methods and swap screens.
     * @param kingsFeast to access its methods.
     */
    MainMenuScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

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

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();
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

    /**
     * Creates a background image for main menu.
     * @return Background image.
     */
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("mainmenubackgroundtitle.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    /**
     * Creates an imagebutton for continuing the game.
     * @return Continue game imagebutton.
     */
    private ImageButton createContinueButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            playUnpressedTexture = kingsFeast.getAssetManager().get("ContinueButton.png");
        } else {
            playUnpressedTexture = kingsFeast.getAssetManager().get("jatkapelia.png");
        }

        ImageButton continueGame =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(playUnpressedTexture)));

        continueGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        continueGame.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH * 2, GAME_HEIGHT / 8);

        // Button's functionality
        continueGame.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                // Load new screen when tapped
                kingsFeast.setScreen(new GameScreen(kingsFeast));
                dispose();
            }
        });

        return continueGame;
    }

    /**
     * Creates an imagebutton for accessing settings screen.
     * @return Settings imagebutton.
     */
    private ImageButton createSettingsButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            settingsTexture = kingsFeast.getAssetManager().get("SettingsButton.png");
        } else {
            settingsTexture = kingsFeast.getAssetManager().get("asetukset.png");
        }

        ImageButton settingsButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)));
        settingsButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 8);
        settingsButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                // Load new screen and pass this screen to enable returning to it
                kingsFeast.setScreen(new OptionsScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });

        return settingsButton;
    }

    /**
     * Creates an imagebutton for accessing how to play screen.
     * @return How to play button.
     */
    private ImageButton createHowToPlayButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            settingsTexture = kingsFeast.getAssetManager().get("HowToPlayButton.png");
        } else {
            settingsTexture = kingsFeast.getAssetManager().get("kuinkapelata.png");
        }

        ImageButton howToPlay =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)));
        howToPlay.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        howToPlay.setPosition(GAME_WIDTH / 2 + BUTTON_WIDTH , GAME_HEIGHT / 8);
        howToPlay.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                // Load new screen
                kingsFeast.setScreen(new TextScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });

        return howToPlay;
    }

    /**
     * Creates an imagebutton for starting a New Game.
     * @return New game imagebutton.
     */
    private ImageButton createNewGameButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            newGameTexture = kingsFeast.getAssetManager().get("NewGameButton.png");
        } else {
            newGameTexture = kingsFeast.getAssetManager().get("uusipeli.png");
        }

        ImageButton newGame =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(newGameTexture)));
        newGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        newGame.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH, GAME_HEIGHT / 8);
        newGame.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                // Create confirmation dialog to ensure player wants to start a new game
                createConfirmationDialog();
            }
        });

        return newGame;
    }

    /**
     * Creates a confirmation dialog used to confirm if the player wants to start a new game.
     *
     * Dialog gets created, buttons are added to it. Buttons have functionality where 'yes' returns
     * 'true' -> save data is reset and a new game starts, 'no' returns 'false' -> gets rid of the
     * dialog and nothing else happens.
     */
    private void createConfirmationDialog() {
        Dialog dialog = new Dialog("",
                new Window.WindowStyle(new BitmapFont(), Color.WHITE, null)) {

            // If player taps 'yes' new game starts and saved data is resetted
            public void result(Object obj) {
                if ((boolean) obj) {
                    kingsFeast.clearSaveState();
                    dispose();
                    kingsFeast.setScreen(new TextScreen(kingsFeast));
                }
            }
        };

        // Set font size and load dialog textures
        BitmapFont font = Util.initFont(36);
        Texture buttonDown = kingsFeast.getAssetManager().get("tyhjanappi.png");
        Texture buttonUp = kingsFeast.getAssetManager().get("tyhjanappi.png");

        TextButton.TextButtonStyle tButtonStyle = new TextButton.TextButtonStyle
                (new TextureRegionDrawable(new TextureRegion(buttonUp)),
                new TextureRegionDrawable(new TextureRegion(buttonDown)),
                new TextureRegionDrawable(new TextureRegion(buttonUp)),
                font);

        // Create textbuttons
        TextButton yesButton = new TextButton(myBundle.get("dialogYes"), tButtonStyle);

        TextButton noButton = new TextButton(myBundle.get("dialogNo"), tButtonStyle);

        // Create dialog text and set what values dialog buttons will return
        dialog.text(new Label(myBundle.get("dialogConfirm"),
                new Label.LabelStyle(font, Color.WHITE)));
        dialog.button(yesButton, true);
        dialog.button(noButton, false);
        // Show dialog
        dialog.show(stage);
    }

    private MainMenuScreen getThisScreen() {
        return this;
    }

    /**
     * Disposes elements after screen change.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

}
