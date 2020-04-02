package fi.tuni.tamk.tiko.kingsfeast;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class FeedbackScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private Texture backgroundTexture;
    private Texture kingTexture;
    private Texture kingSpeech;
    private Stage stage;
    private final float BUTTON_WIDTH = 300f;
    private final float BUTTON_HEIGHT = 96f;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private final int FONT_SIZE = 36;

    private Texture okTexture;
    private String throwAmount;
    private String foodWasteAmount;
    private String score = "0";
    private String totalThrowAmount;
    private String riverPollutionLevel;
    private BitmapFont bitmapFont;
    private Viewport viewport;
    private Table table;

    public FeedbackScreen(KingsFeast kingsFeast, int throwAmount, int visitorsServed) {
        this.kingsFeast = kingsFeast;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        font = Util.initFont(FONT_SIZE);

        // aMuikku lisäsi
        kingsFeast.getPrefs().putInteger("totalThrows",
                kingsFeast.getPrefs().getInteger("totalThrows") + throwAmount);
        kingsFeast.getPrefs().flush();

        this.throwAmount = Integer.toString(throwAmount);

        // aMuikku lisäsi
        totalThrowAmount = Integer.toString(kingsFeast.getPrefs().getInteger("totalThrows"));

        foodWasteAmount = Integer.toString(throwAmount - visitorsServed);
        calculateScore(throwAmount, visitorsServed);

        viewport = new FitViewport(GAME_WIDTH,
                GAME_HEIGHT,
                new OrthographicCamera());

        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createBackgroundImage());
        stage.addActor(createKingImage());
        stage.addActor(createKingSpeech());
        stage.addActor(createOkButton());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        stage.act(delta);
        stage.draw();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "Throws in the last level: " + throwAmount, GAME_WIDTH / 2, GAME_HEIGHT - 100);
        font.draw(batch, "Food Waste: " + foodWasteAmount, GAME_WIDTH / 2, GAME_HEIGHT - 200);
        font.draw(batch, "Score: " + score, GAME_WIDTH / 2, GAME_HEIGHT - 300);
        font.draw(batch, "Pollution Level: " + riverPollutionLevel, GAME_WIDTH / 2, GAME_HEIGHT - 400);
        font.draw(batch, "Total Throws: " + totalThrowAmount, GAME_WIDTH / 2, GAME_HEIGHT - 500);
        batch.end();
    }

    private Image createBackgroundImage() {
        backgroundTexture = new Texture("options.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private Image createKingImage() {
        kingTexture = new Texture("kingplaceholder.png");
        Image king = new Image(kingTexture);
        king.setSize(kingTexture.getWidth(), kingTexture.getHeight());
        king.setPosition(kingTexture.getWidth() / 2, kingTexture.getHeight() / 3);
        return king;
    }

    private Image createKingSpeech() {
        kingSpeech = new Texture("kingspeech.png");
        Image kingSpeechBubble = new Image(kingSpeech);
        //kingSpeechBubble.setSize(kingTexture.getWidth() - 50, kingTexture.getHeight() - 50);
        kingSpeechBubble.setPosition(GAME_HEIGHT / 2, GAME_HEIGHT - kingSpeech.getHeight(), Align.center);
        return kingSpeechBubble;
    }

    private ImageButton createOkButton() {
        okTexture = new Texture("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setPosition((GAME_WIDTH / 2) + (GAME_WIDTH / 4), GAME_HEIGHT / 6);
        ok.setSize(300f, 150f);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.saveGameOnLevelSwap();
                dispose();
                kingsFeast.setScreen(new PollutionScreen(kingsFeast));
            }
        });
        return ok;
    }

    void calculateScore(int throwes, int served) {
        int waste = throwes - served;
        int scores = 0;
        if(waste < 5) {
            scores = 1000;
        } else if(waste >= 5 && waste <= 10) {
            scores = 700;
        } else if(waste > 10 && waste <= 20) {
            scores = 200;
        }

        score = Integer.toString(scores);

        setPollution(scores);
    }

    void setPollution(int scoring) {
        if (scoring == 200) {
            riverPollutionLevel = "80/100";
        } else if (scoring == 700) {
            riverPollutionLevel = "50/100";
        } else if (scoring == 1000){
            riverPollutionLevel = "0/100";
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        okTexture.dispose();
        kingTexture.dispose();
        kingSpeech.dispose();
        backgroundTexture.dispose();
        batch.dispose();
        font.dispose();
    }
}
