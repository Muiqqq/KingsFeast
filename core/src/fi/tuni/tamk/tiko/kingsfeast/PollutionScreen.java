package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class PollutionScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private final float BUTTON_WIDTH = 300f;
    private final float BUTTON_HEIGHT = 150f;
    private final int FONT_SIZE = 48;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Stage stage;
    private Texture okTexture;
    private Texture kingSpeech;
    private boolean gameWon;
    private boolean gameLost;
    private OrthographicCamera camera;

    // DOCUMENTATION

    public PollutionScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
        batch = kingsFeast.getSpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        gameWon = false;
        gameLost = false;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        font = Util.initFont(FONT_SIZE);
        stage.addActor(createBackgroundImage());
        stage.addActor(createOkButton());
        checkGameEnd();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if(gameWon) {
            font.draw(batch, "You have saved the river!", GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 250);
            font.draw(batch, "You have succeeded in teaching\n the king better ways to deal\n with food waste!\n\n\nFinal score: " + kingsFeast.getTotalScore(), GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 100);
        } else if (gameLost) {
            font.draw(batch, "Game over!\n\n\n The river pollution level\n has reached critical point!\n\nFinal score: " + kingsFeast.getTotalScore(), GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 250);
        } else {
            font.draw(batch, "Pollution Level: " + kingsFeast.getPollutionLevel() + "/100", GAME_WIDTH / 4, GAME_HEIGHT - BUTTON_HEIGHT * 2);
        }
        batch.end();
    }

    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ok.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, BUTTON_HEIGHT - 50);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if(gameWon || gameLost) {
                    kingsFeast.clearSaveState();
                    kingsFeast.setScreen(new MainMenuScreen(kingsFeast));
                } else {
                    kingsFeast.setScreen(new GameScreen(kingsFeast));
                }
                dispose();
            }
        });
        return ok;
    }

    private void checkGameEnd() {
        int pollution = Integer.parseInt(kingsFeast.getPollutionLevel());
        if(pollution >= 100) {
            gameLost();
        } else if (pollution <= 0) {
            gameWon();
        }
    }

    private void gameLost() {
        stage.addActor(createKingSpeech());
        stage.addActor(createOkButton());
        gameLost = true;
    }

    private void gameWon() {
        stage.addActor(createKingSpeech());
        stage.addActor(createOkButton());
        gameWon = true;
    }

    private Image createKingSpeech() {
        kingSpeech = kingsFeast.getAssetManager().get("tekstitaustahorizontal.png");
        Image kingSpeechBubble = new Image(kingSpeech);
        kingSpeechBubble.setSize(kingSpeech.getWidth() - 90, kingSpeech.getHeight() - 90);
        kingSpeechBubble.setPosition(GAME_WIDTH / 2 - kingSpeech.getWidth() / 2 + 50, GAME_HEIGHT / 2 - kingSpeech.getHeight() / 2 + 50);
        return kingSpeechBubble;
    }


    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
