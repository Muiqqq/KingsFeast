package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class PauseScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private final GameScreen gameScreen;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private Texture backgroundTexture;
    private Stage stage;
    private final float BUTTON_WIDTH = 500f;
    private final float BUTTON_HEIGHT = 120f;

    private Texture continueBtnTexture;
    private Texture mainMenuBtnTexture;
    private Texture settingsButtonTexture;
    private Texture exitButtonTexture;

    // Figure out how to save the GameScreen to be continued where left off

    public PauseScreen(KingsFeast kingsFeast, GameScreen gameScreen) {
        this.kingsFeast = kingsFeast;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createContinueButton());
        stage.addActor(createMainMenuButton());
        stage.addActor(createSettingsButton());
        stage.addActor(createExitButton());

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
        backgroundTexture = kingsFeast.getAssetManager().get("options.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createContinueButton() {
        continueBtnTexture = kingsFeast.getAssetManager().get("StartGameButton.png");
        ImageButton continueButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(continueBtnTexture)));
        continueButton.setPosition(GAME_WIDTH, GAME_HEIGHT, Align.center);
        continueButton.setSize(150f, 75f);
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

    private ImageButton createMainMenuButton() {
        mainMenuBtnTexture = kingsFeast.getAssetManager().get("credits.png");
        ImageButton mainMenuButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(mainMenuBtnTexture)));
        mainMenuButton.setPosition(GAME_WIDTH, GAME_HEIGHT -150, Align.center);
        mainMenuButton.setSize(150f, 75f);
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

    private ImageButton createSettingsButton() {
        settingsButtonTexture = kingsFeast.getAssetManager().get("SettingsButton.png");
        ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsButtonTexture)));
        settingsButton.setPosition(GAME_WIDTH, GAME_HEIGHT -300, Align.center);
        settingsButton.setSize(150f, 75f);
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