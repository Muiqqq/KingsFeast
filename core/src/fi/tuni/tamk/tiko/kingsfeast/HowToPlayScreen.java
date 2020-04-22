package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class HowToPlayScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private Stage stage;
    private final float BUTTON_WIDTH = 550f;
    private final float BUTTON_HEIGHT = 150f;

    private final Screen previousScreen;
    private SpriteBatch batch;

    // Textures
    private Texture backgroundTexture;
    private Texture textBackground;
    private Texture okTexture;

    private String myText;

    private final int FONT_SIZE = 48;
    private BitmapFont text;

    // Localization
    I18NBundle myBundle;

    public HowToPlayScreen(KingsFeast kingsFeast, Screen screen) {
        this.kingsFeast = kingsFeast;
        previousScreen = screen;
        initFont();
        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();
        myText = myBundle.get("howToPlay");

        batch = kingsFeast.getSpriteBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createTextBackground());
        stage.addActor(createOkButton());

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        batch.begin();
        text.draw(batch, myText, 125, GAME_HEIGHT - 175);
        batch.end();
    }

    // Returns background image
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    // Returns background texture to text
    private Image createTextBackground() {
        textBackground = kingsFeast.getAssetManager().get("howtoplaybg.png");
        Image textBg = new Image(textBackground);
        textBg.setSize(textBackground.getWidth(), textBackground.getHeight());
        textBg.setPosition(GAME_WIDTH / 2 - textBackground.getWidth() / 2 , GAME_HEIGHT / 2 - textBackground.getHeight() / 2);
        return textBg;
    }

    // Returns ok imagebutton
    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ok.setPosition(GAME_WIDTH / 2, BUTTON_HEIGHT + 50, Align.center);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(previousScreen);
            }
        });
        return ok;
    }

    private void initFont() {
        FreeTypeFontGenerator textFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter textFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        textFontParameter.size = FONT_SIZE;
        textFontParameter.color = Color.BLACK;
        text = textFontGenerator.generateFont(textFontParameter);
    }

    @Override
    public void dispose() {
        stage.dispose();
        text.dispose();
    }
}
