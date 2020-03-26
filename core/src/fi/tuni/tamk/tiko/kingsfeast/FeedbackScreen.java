package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.utils.viewport.Viewport;

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

    private Texture okTexture;
    private String throwAmount = "50";
    private String foodWasteAmount = "8";
    private String score = "1200";


    public FeedbackScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
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
        font.draw(batch, "Throw Amount: " + throwAmount, 500, 400);
        font.draw(batch, "Food Waste Amount: " + foodWasteAmount, 500, 360);
        font.draw(batch, "Score: " + score, 500, 320);
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
                kingsFeast.setScreen(new PollutionScreen(kingsFeast));
                dispose();
            }
        });
        return ok;
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
