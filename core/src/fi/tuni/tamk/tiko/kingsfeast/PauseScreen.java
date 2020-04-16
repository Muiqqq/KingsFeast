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
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class PauseScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private final GameScreen gameScreen;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private Stage stage;
    private final float BUTTON_WIDTH = 550f;
    private final float BUTTON_HEIGHT = 150f;

    // Textures
    private Texture continueBtnTexture;
    private Texture newGameTexture;
    private Texture settingsButtonTexture;
    private Texture exitButtonTexture;
    private Texture backgroundTexture;
    private Texture scrollBg;
    private Texture howToPlayTexture;

    I18NBundle myBundle;


    public PauseScreen(KingsFeast kingsFeast, GameScreen gameScreen) {
        this.kingsFeast = kingsFeast;
        this.gameScreen = gameScreen;
        myBundle = kingsFeast.langManager.getCurrentBundle();
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createScroll());
        stage.addActor(createContinueButton());
        stage.addActor(createNewGameButton());
        stage.addActor(createSettingsButton());
        stage.addActor(createHowToPlayButton());

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

    // Return image of a scroll
    private Image createScroll() {
        scrollBg = kingsFeast.getAssetManager().get("tausta.png");
        Image scroll = new Image(scrollBg);
        scroll.setSize(scrollBg.getWidth() - 250, scrollBg.getHeight() - 250);
        scroll.setPosition(GAME_WIDTH / 2 - scroll.getWidth() / 2, GAME_HEIGHT / 2 - scroll.getHeight() / 2);
        return scroll;
    }

    // Returns continue imagebutton
    private ImageButton createContinueButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            continueBtnTexture = kingsFeast.getAssetManager().get("ContinueButton.png");
        } else {
            continueBtnTexture = kingsFeast.getAssetManager().get("jatkapelia.png");
        }
        ImageButton continueButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(continueBtnTexture)));
        continueButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        continueButton.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 3);
        continueButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                dispose();
                kingsFeast.setScreen(gameScreen);
            }
        });
        return continueButton;
    }

    // Return new game imagebutton
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
        newGame.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 4);
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

    // Returns how to play imagebutton
    private ImageButton createHowToPlayButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            howToPlayTexture = kingsFeast.getAssetManager().get("HowToPlayButton.png");
        } else {
            howToPlayTexture = kingsFeast.getAssetManager().get("kuinkapelata.png");
        }

        ImageButton howToPlay =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(howToPlayTexture)));
        howToPlay.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        howToPlay.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 5);
        howToPlay.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent e, float x, float y, int count, int button) {
                super.tap(e, x, y, count, button);
                // Load new screen
                kingsFeast.setScreen(new HowToPlayScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });

        return howToPlay;
    }

    // Creates confirmation dialog to ensure if player wants to start a new game
    private void createConfirmationDialog() {
        Dialog dialog = new Dialog("",
                new Window.WindowStyle(new BitmapFont(), Color.WHITE, null)) {

            // If player taps 'yes' new game starts and saved data is resetted
            public void result(Object obj) {
                if ((boolean) obj) {
                    kingsFeast.clearSaveState();
                    dispose();
                    kingsFeast.setScreen(new GameScreen(kingsFeast));
                }
            }
        };

        // Set font size and load dialog tetures
        BitmapFont font = Util.initFont(36);
        Texture buttonDown = kingsFeast.getAssetManager().get("tyhjanappi.png");
        Texture buttonUp = kingsFeast.getAssetManager().get("tyhjanappi.png");

        // Create textbuttons
        TextButton yesButton = new TextButton(myBundle.get("dialogYes"), new TextButton.TextButtonStyle(
                new TextureRegionDrawable(new TextureRegion(buttonUp)),
                new TextureRegionDrawable(new TextureRegion(buttonDown)),
                new TextureRegionDrawable(new TextureRegion(buttonUp)),
                font));

        TextButton noButton = new TextButton(myBundle.get("dialogNo"), new TextButton.TextButtonStyle(
                new TextureRegionDrawable(new TextureRegion(buttonUp)),
                new TextureRegionDrawable(new TextureRegion(buttonDown)),
                new TextureRegionDrawable(new TextureRegion(buttonUp)),
                font));

        // Create dialog text and set what values dialog buttons will return
        dialog.text(new Label(myBundle.get("dialogConfirm"),
                new Label.LabelStyle(font, Color.WHITE)));
        dialog.button(yesButton, true);
        dialog.button(noButton, false);
        // Show dialog
        dialog.show(stage);
    }

    // Returns settings imagebutton
    private ImageButton createSettingsButton() {
        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            settingsButtonTexture = kingsFeast.getAssetManager().get("SettingsButton.png");
        } else {
            settingsButtonTexture = kingsFeast.getAssetManager().get("asetukset.png");
        }
        ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsButtonTexture)));
        settingsButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 6);
        settingsButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new OptionsScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });
        return settingsButton;
    }

    // Returns exit game imagebutton
    private ImageButton createExitButton() {
        exitButtonTexture = kingsFeast.getAssetManager().get("credits.png");
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitButtonTexture)));
        exitButton.setPosition(GAME_WIDTH, GAME_HEIGHT -400, Align.center);
        exitButton.setSize(150f, 75f);
        exitButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Gdx.app.exit();
                dispose();
            }
        });
        return exitButton;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private PauseScreen getThisScreen() {
        return this;
    }
}