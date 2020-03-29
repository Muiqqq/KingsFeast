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

public class PauseScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
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

    public PauseScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

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
        backgroundTexture = new Texture("options.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createContinueButton() {
        continueBtnTexture = new Texture("StartGameButton.png");
        ImageButton continueButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(continueBtnTexture)));
        continueButton.setPosition(GAME_WIDTH, GAME_HEIGHT, Align.center);
        continueButton.setSize(150f, 75f);
        continueButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new GameScreen(kingsFeast));
                dispose();
            }
        });
        return continueButton;
    }

    private ImageButton createMainMenuButton() {
        mainMenuBtnTexture = new Texture("credits.png");
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
        settingsButtonTexture = new Texture("SettingsButton.png");
        ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsButtonTexture)));
        settingsButton.setPosition(GAME_WIDTH, GAME_HEIGHT -300, Align.center);
        settingsButton.setSize(150f, 75f);
        settingsButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new OptionsScreen(kingsFeast));
                dispose();
            }
        });
        return settingsButton;
    }

    private ImageButton createExitButton() {
        exitButtonTexture = new Texture("credits.png");
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
        backgroundTexture.dispose();
    }
}