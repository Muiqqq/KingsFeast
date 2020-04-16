package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class PlayerIntro extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private Stage stage;
    private final float BUTTON_WIDTH = 550f;
    private final float BUTTON_HEIGHT = 150f;
    private final int FONT_SIZE = 48;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    I18NBundle myBundle;
    private Texture textBg;
    String[] introTexts;

    // Textures
    private Texture backgroundTexture;
    private BitmapFont font;

    public PlayerIntro(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
        batch = kingsFeast.getSpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        font = Util.initFont(FONT_SIZE);

        // Add all buttons to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createTextBg());

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
        font.draw(batch, myBundle.get("gameLost")+ ": " + kingsFeast.getTotalScore(), GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 250);
        batch.end();
    }

    // Returns background image
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    // Returns background texture for the introduction text
    private Image createTextBg() {
        textBg = kingsFeast.getAssetManager().get("taustavaaka.png");
        Image textBackGround = new Image(textBg);
        textBackGround.setSize(textBg.getWidth(), textBg.getHeight() - 50);
        textBackGround.setPosition(GAME_WIDTH / 2 - textBg.getWidth() / 2 + 50, GAME_HEIGHT / 2 - textBg.getHeight() / 2 + 50);
        return textBackGround;
    }


    @Override
    public void dispose() {
        stage.dispose();
    }
}
