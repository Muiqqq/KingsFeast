package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * TextScreen class handles Player intro screen and How to Play screen creation.
 */
public class TextScreen extends ScreenAdapter {
    // Game object
    private final KingsFeast kingsFeast;

    // Screen stuff
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private Stage stage;
    private OrthographicCamera camera;

    private final Screen previousScreen;
    private SpriteBatch batch;

    // Textures
    private Texture backgroundTexture;
    private Texture textBackground;

    // Text to be shown
    private String myText;

    // Font stuff
    private final int FONT_SIZE = 45;
    private BitmapFont text;

    // Boolean to check if text to be shown is intro text or how to play
    private boolean intro;

    // Localization
    private I18NBundle myBundle;

    /**
     * Constructor for how to play screen.
     * @param kingsFeast to access its methods
     * @param screen to enable possibility to go back to previous screen without
     *               having to create the previous screen again.
     */
    TextScreen(KingsFeast kingsFeast, Screen screen) {
        this.kingsFeast = kingsFeast;
        previousScreen = screen;
        batch = kingsFeast.getSpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        initFont();
        intro = false;

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();
        myText = myBundle.get("howToPlay");
    }

    /**
     * Constructor for player intro screen.
     * @param kingsFeast to access its methods.
     */
    TextScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
        previousScreen = null;
        batch = kingsFeast.getSpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        initFont();
        intro = true;

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();
        myText = myBundle.get("introText");
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createTextBackground());
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
        text.draw(batch, myText, 125, GAME_HEIGHT - 160);
        batch.end();

        // Listen for taps and change screen accordingly
        // Intro boolean checks if current screen is intro screen or not
        if(Gdx.input.isTouched() && intro) {
            kingsFeast.setScreen(new GameScreen(kingsFeast));
            dispose();
        } else if(Gdx.input.isTouched() && !intro) {
            kingsFeast.setScreen(previousScreen);
            dispose();
        }
    }

    /**
     * Creates a background image for the Stage.
     * @return Background image.
     */
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    /**
     * Creates a background image for the text.
     * @return Text background image.
     */
    private Image createTextBackground() {
        textBackground = kingsFeast.getAssetManager().get("howtoplaybg.png");
        Image textBg = new Image(textBackground);
        textBg.setSize(textBackground.getWidth(), textBackground.getHeight());
        textBg.setPosition(GAME_WIDTH / 2 - textBackground.getWidth() / 2 , GAME_HEIGHT / 2 - textBackground.getHeight() / 2);
        return textBg;
    }

    /**
     * Initializes the font to be used. Size and color.
     */
    private void initFont() {
        FreeTypeFontGenerator textFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter textFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        textFontParameter.size = FONT_SIZE;
        textFontParameter.color = Color.BLACK;
        text = textFontGenerator.generateFont(textFontParameter);
    }

    /**
     * Disposes elements after screen change.
     */
    @Override
    public void dispose() {
        stage.dispose();
        text.dispose();
    }
}
