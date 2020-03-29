package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class FeedbackScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private Texture backgroundTexture;
    private Texture kingTexture;
    private Texture kingSpeech;
    private Stage stage;
    private final float BUTTON_WIDTH = 128f;
    private final float BUTTON_HEIGHT = 96f;
    private SpriteBatch batch;
    private BitmapFont font;
    private int visitorsServed;

    private Texture okTexture;
    private String throwAmount;
    private String foodWasteAmount;
    private String score = "0";
    private String totalThrowAmount;
    private String riverPollutionLevel;


    public FeedbackScreen(KingsFeast kingsFeast, int throwAmount, int visitorsServed) {
        this.kingsFeast = kingsFeast;

        // aMuikku lisäsi
        kingsFeast.getPrefs().putInteger("totalThrows",
                kingsFeast.getPrefs().getInteger("totalThrows") + throwAmount);
        kingsFeast.getPrefs().flush();

        this.throwAmount = Integer.toString(throwAmount);

        // aMuikku lisäsi
        totalThrowAmount = Integer.toString(kingsFeast.getPrefs().getInteger("totalThrows"));

        foodWasteAmount = Integer.toString(throwAmount - visitorsServed);
        calculateScore(throwAmount, visitorsServed);
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);

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
        batch.begin();
        font.draw(batch, "Total throws: " + totalThrowAmount, 500, 440);
        font.draw(batch, "Throws in This Level: " + throwAmount, 500, 400);
        font.draw(batch, "Food Waste Amount: " + foodWasteAmount, 500, 360);
        font.draw(batch, "Score: " + score, 500, 320);
        font.draw(batch, "River Pollution Level: " + riverPollutionLevel, 500, 250);
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
        king.setSize(kingTexture.getWidth() / 3, kingTexture.getHeight() / 3);
        king.setPosition(140, 140, Align.center);
        return king;
    }

    private Image createKingSpeech() {
        kingSpeech = new Texture("kingspeech.png");
        Image kingSpeechBubble = new Image(kingSpeech);
        //kingSpeechBubble.setSize(kingTexture.getWidth() - 50, kingTexture.getHeight() - 50);
        kingSpeechBubble.setPosition(230, 330, Align.center);
        return kingSpeechBubble;
    }

    private ImageButton createOkButton() {
        okTexture = new Texture("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setPosition(GAME_WIDTH - 310, GAME_HEIGHT / 4);
        ok.setSize(150f, 75f);
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
