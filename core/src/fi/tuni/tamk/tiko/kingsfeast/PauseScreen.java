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
    private Texture mainMenuBtnTexture;
    private Texture settingsButtonTexture;
    private Texture exitButtonTexture;
    private Texture backgroundTexture;
    private Texture scrollBg;


    public PauseScreen(KingsFeast kingsFeast, GameScreen gameScreen) {
        this.kingsFeast = kingsFeast;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createScroll());
        stage.addActor(createContinueButton());
       // stage.addActor(createMainMenuButton());
        stage.addActor(createSettingsButton());
        //stage.addActor(createExitButton());

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
        continueButton.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 3 - 50);
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

    // Returns main menu imagebutton
    private ImageButton createMainMenuButton() {
        mainMenuBtnTexture = kingsFeast.getAssetManager().get("MainMenuButton.png");
        ImageButton mainMenuButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(mainMenuBtnTexture)));
        mainMenuButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        mainMenuButton.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 5);
        mainMenuButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new MainMenuScreen(kingsFeast));
                dispose();
            }
        });
        return mainMenuButton;
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
        settingsButton.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 5);
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