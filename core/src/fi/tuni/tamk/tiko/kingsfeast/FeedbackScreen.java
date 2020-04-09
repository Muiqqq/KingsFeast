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
    private final float BUTTON_WIDTH = 500f;
    private final float BUTTON_HEIGHT = 120f;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont bitmapFont;
    private BitmapFont speechFont;
    private BitmapFont negativeFont;
    private BitmapFont positiveFont;
    private OrthographicCamera camera;
    private final int FONT_SIZE = 36;
    private final int SPEECH_FONT_SIZE = 48;

    private Texture okTexture;
    private String throwAmount;
    private String foodWasteAmount;
    private Viewport viewport;

    private Texture pigsTexture;
    private Texture compostTexture;
    private Texture poorTexture;

    private int levelScoreCounter;
    private int totalScoreCounter;
    private int pollutionCounter;
    private boolean toTotalScore;

    private Texture pigsDisabledTexture;
    private Texture compostDisabledTexture;
    private Texture poorDisabledTexture;

    private String kingDialogue = "Well done my loyal servant!\nAlmost no foodwaste!";

    public FeedbackScreen(KingsFeast kingsFeast, int throwAmount, int visitorsServed) {
        this.kingsFeast = kingsFeast;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        bitmapFont = new BitmapFont();
        speechFont = new BitmapFont();
        negativeFont = new BitmapFont();
        positiveFont = new BitmapFont();
        initFonts();
        levelScoreCounter = 0;
        totalScoreCounter = Integer.parseInt(kingsFeast.getTotalScore());
        toTotalScore = false;

        // aMuikku lisäsi
        kingsFeast.getPrefs().putInteger("totalThrows",
                kingsFeast.getPrefs().getInteger("totalThrows") + throwAmount);
        kingsFeast.getPrefs().flush();

        this.throwAmount = Integer.toString(throwAmount);

        foodWasteAmount = Integer.toString(throwAmount - visitorsServed);
        kingsFeast.calculateScore(throwAmount, visitorsServed);
        pollutionCounter = Integer.parseInt(kingsFeast.getPollutionLevel());

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
        stage.addActor(createPigsLifeline());
        stage.addActor(createCompostLifeLine());
        stage.addActor(createPoorLifeLine());
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
        font.draw(batch, "Throws in the last level: " + throwAmount, GAME_WIDTH / 2 + 250, GAME_HEIGHT - 100);
        font.draw(batch, "Food Waste: " + foodWasteAmount, GAME_WIDTH / 2 + 250, GAME_HEIGHT - 200);
        font.draw(batch, "Level Score: " + kingsFeast.getLevelScore(), GAME_WIDTH / 2 + 250, GAME_HEIGHT - 300);
            /*if (this.levelScoreCounter < Integer.parseInt(this.kingsFeast.getLevelScore())) {
                this.levelScoreCounter += 10;
            } else {
                toTotalScore = true;
            }*/
        font.draw(batch, "Pollution Level: " + kingsFeast.getPollutionLevel(), GAME_WIDTH / 2 + 250, GAME_HEIGHT - 400);
            drawPollutionEffect();
        font.draw(batch, "Total Throws: " + kingsFeast.getTotalThrows(), GAME_WIDTH / 2 + 250, GAME_HEIGHT - 500);
        font.draw(batch, "Total Score: " + totalScoreCounter, GAME_WIDTH / 2 + 250, GAME_HEIGHT - 600);
        if (this.totalScoreCounter < Integer.parseInt(this.kingsFeast.getTotalScore())) {
            this.totalScoreCounter += 10;
        } else if(this.totalScoreCounter > Integer.parseInt(this.kingsFeast.getTotalScore())) {
            this.totalScoreCounter -= 10;
        }
        speechFont.draw(batch, kingDialogue, 50, GAME_HEIGHT - 60);
        batch.end();
    }

    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private Image createKingImage() {
        kingTexture = kingsFeast.getAssetManager().get("kingplaceholder.png");
        Image king = new Image(kingTexture);
        king.setSize(kingTexture.getWidth(), kingTexture.getHeight());
        king.setPosition(kingTexture.getWidth() / 2, kingTexture.getHeight() / 3);
        return king;
    }

    private Image createKingSpeech() {
        kingSpeech = kingsFeast.getAssetManager().get("kingspeech.png");
        Image kingSpeechBubble = new Image(kingSpeech);
        kingSpeechBubble.setSize(kingSpeech.getWidth() - 50, kingSpeech.getHeight() - 50);
        kingSpeechBubble.setPosition(GAME_HEIGHT / 2, GAME_HEIGHT - kingSpeech.getHeight() / 2, Align.center);
        return kingSpeechBubble;
    }

    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
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

    private void initFonts() {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = FONT_SIZE;
        fontParameter.borderWidth = 4;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        FreeTypeFontGenerator speechFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter speechFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        speechFontParameter.size = SPEECH_FONT_SIZE;
        speechFontParameter.borderWidth = 4;
        speechFontParameter.borderColor = Color.BLACK;
        speechFontParameter.color = Color.WHITE;
        speechFont = speechFontGenerator.generateFont(speechFontParameter);

        FreeTypeFontGenerator negativeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter negativeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        negativeFontParameter.size = FONT_SIZE;
        negativeFontParameter.color = Color.RED;
        negativeFont = negativeFontGenerator.generateFont(negativeFontParameter);

        FreeTypeFontGenerator positiveFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter positiveFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        positiveFontParameter.size = FONT_SIZE;
        positiveFontParameter.color = Color.GREEN;
        positiveFont = positiveFontGenerator.generateFont(positiveFontParameter);
    }

    private ImageButton createPigsLifeline() {
        int totalScore = Integer.parseInt(kingsFeast.getTotalScore());
        if(totalScore >= 1000) {
            pigsTexture = kingsFeast.getAssetManager().get("pigsplaceholder.png");
        } else {
            pigsTexture = kingsFeast.getAssetManager().get("pigsdisabledplaceholder.png");
        }

        ImageButton pigsLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(pigsTexture)));
        pigsLifeline.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, 400);
        pigsLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        pigsLifeline.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                kingsFeast.setPollutionLevel(-5);
                kingsFeast.setTotalScore(-1000);
            }
        });
        return pigsLifeline;
    }

    private ImageButton createCompostLifeLine() {
        int totalScore  = Integer.parseInt(kingsFeast.getTotalScore());
        if(totalScore >= 2000) {
            compostTexture = kingsFeast.getAssetManager().get("compostplaceholder.png");
        } else {
            compostTexture = kingsFeast.getAssetManager().get("compostdisabledplaceholder.png");
        }
        ImageButton compostLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(compostTexture)));
        compostLifeline.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, 250);
        compostLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        compostLifeline.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                kingsFeast.setPollutionLevel(-10);
                kingsFeast.setTotalScore(-2000);
            }
        });
        return compostLifeline;
    }

    private ImageButton createPoorLifeLine() {
        int totalScore  = Integer.parseInt(kingsFeast.getTotalScore());
        if(totalScore >= 3000) {
            poorTexture = kingsFeast.getAssetManager().get("poorplaceholder.png");
        } else {
            poorTexture = kingsFeast.getAssetManager().get("poordisabledplaceholder.png");
        }
        ImageButton poorLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(poorTexture)));
        poorLifeline.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, 100);
        poorLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        poorLifeline.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                kingsFeast.setPollutionLevel(-15);
                kingsFeast.setTotalScore(-2500);
                dispose();
            }
        });
        return poorLifeline;
    }

    private void drawPollutionEffect() {
        int oldPol = kingsFeast.getOldPollution();
        int newPol = Integer.parseInt(kingsFeast.getPollutionLevel());
        if(oldPol < newPol) {
            negativeFont.draw(batch, "+" + (newPol - oldPol), GAME_WIDTH - 250, GAME_HEIGHT - 400);
        } else if (newPol < oldPol) {
            positiveFont.draw(batch, "-" + (oldPol - newPol), GAME_WIDTH -250, GAME_HEIGHT - 400);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        speechFont.dispose();
    }
}
