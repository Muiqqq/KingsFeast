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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class FeedbackScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;

    // Screen stuff
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private final float BUTTON_WIDTH = 500f;
    private final float BUTTON_HEIGHT = 120f;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    private ImageButton pigsLifeline;
    private ImageButton compostLifeline;
    private ImageButton poorLifeline;

    // Textures
    private Texture backgroundTexture;
    private Texture kingTexture;
    private Texture kingSpeech;
    private Texture scroll;
    private Texture okTexture;
    private Texture pigsTexture;
    private Texture compostTexture;
    private Texture poorTexture;
    private Texture pigsDisabledTexture;
    private Texture compostDisabledTexture;
    private Texture poorDisabledTexture;

    // Fonts
    private BitmapFont font;
    private BitmapFont speechFont;
    private BitmapFont negativeFont;
    private BitmapFont positiveFont;
    private final int FONT_SIZE = 38;
    private final int SPEECH_FONT_SIZE = 42;

    // Localization
    I18NBundle myBundle;

    // Strings
    private String throwAmount;
    private String foodWasteAmount;
    private String foodWaste;
    private String throwsInLevel;
    private String levelScore;
    private String pollutionLevel;
    private String totalThrows;
    private String totalScore;
    private String kingDialogue;
    private String story;

    // Game Data
    private boolean isPigsUsed;
    private boolean isCompostUsed;
    private boolean isPoorUsed;
    private int levelScoreCounter;
    private int totalScoreCounter;
    private int pollutionCounter;
    private boolean toTotalScore;

    // Constructor receives game object to access it. Also 2 ints from gamescreen for accessing

    /**
     * Constructor receives game object and two integers for game data calculation purposes.
     * It sets the camera for stage2D and gets current language to display correct text.
     * It also initializes some variables and booleans for game progress tracking.
     * @param kingsFeast The Game object to access its methods.
     * @param throwAmount Amount of food thrown in the last level.
     * @param visitorsServed Amount of visitors served in the last level.
     */
    public FeedbackScreen(KingsFeast kingsFeast, int throwAmount, int visitorsServed) {
        this.kingsFeast = kingsFeast;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        initFonts();

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();
        foodWaste = myBundle.get("foodWaste");
        throwsInLevel = myBundle.get("throwsInLevel");
        levelScore = myBundle.get("levelScore");
        pollutionLevel = myBundle.get("pollutionLevel");
        totalThrows = myBundle.get("totalThrows");
        totalScore = myBundle.get("totalScore");
        kingDialogue = myBundle.get("kingDialoguePlaceholder");

        // Initialize game data for showing correct data for player
        levelScoreCounter = 0;
        totalScoreCounter = Integer.parseInt(kingsFeast.getTotalScore());
        toTotalScore = false;
        isPigsUsed = false;
        isCompostUsed = false;
        isPoorUsed = false;
        this.throwAmount = Integer.toString(throwAmount);
        foodWasteAmount = Integer.toString(throwAmount - visitorsServed);
        kingsFeast.calculateScore(throwAmount, visitorsServed);
        pollutionCounter = Integer.parseInt(kingsFeast.getPollutionLevel());

        // Check what story text is to be shown on screen
        checkStoryPoints();

        // Stage2D stuff
        viewport = new FitViewport(GAME_WIDTH,
                GAME_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all actors to the stage
        stage.addActor(createBackgroundImage());
        stage.addActor(createKingImage());
        stage.addActor(createKingSpeechBg());
        stage.addActor(createOkButton());

        // Check what lifelines are to be shown on screen (this depends on what story points
        //have been triggered).
        if(kingsFeast.isStoryPointShown(1) || kingsFeast.isStoryPointShown(2) || kingsFeast.isStoryPointShown(3) || kingsFeast.isStoryPointShown(4)) {
            stage.addActor(createPigsLifeline());
        }
        if(kingsFeast.isStoryPointShown(2) || kingsFeast.isStoryPointShown(3) || kingsFeast.isStoryPointShown(4) || kingsFeast.isStoryPointShown(5)) {
            stage.addActor(createCompostLifeLine());
        }
        if(kingsFeast.isStoryPointShown(3) || kingsFeast.isStoryPointShown(4) || kingsFeast.isStoryPointShown(6) || kingsFeast.isStoryPointShown(7)) {
            stage.addActor(createPoorLifeLine());
        }
        stage.addActor(createScroll());
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

        // Draw all text to screen
        font.draw(batch, throwsInLevel + ": " + throwAmount, GAME_WIDTH / 2 + 220, GAME_HEIGHT - 200);
        font.draw(batch, foodWaste + ": " + foodWasteAmount, GAME_WIDTH / 2 + 220, GAME_HEIGHT - 300);
        font.draw(batch, levelScore + ": " + kingsFeast.getLevelScore(), GAME_WIDTH / 2 + 220, GAME_HEIGHT - 400);
        font.draw(batch, pollutionLevel + ": " + kingsFeast.getPollutionLevel(), GAME_WIDTH / 2 + 220, GAME_HEIGHT - 500);
            drawPollutionEffect();
        font.draw(batch, totalThrows + ": " + kingsFeast.getTotalThrows(), GAME_WIDTH / 2 + 220, GAME_HEIGHT - 600);
        font.draw(batch, totalScore + ": " + totalScoreCounter, GAME_WIDTH / 2 + 220, GAME_HEIGHT - 700);
        // If statement to increase score on screen to give player tangible feedback on changed data
        if (this.totalScoreCounter < Integer.parseInt(this.kingsFeast.getTotalScore())) {
            this.totalScoreCounter += 10;
        } else if(this.totalScoreCounter > Integer.parseInt(this.kingsFeast.getTotalScore())) {
            this.totalScoreCounter -= 10;
        }
        //speechFont.draw(batch, kingDialogue, 50, GAME_HEIGHT - 60);
        showStoryPoint();
        batch.end();
        checkIfGameEnd();
    }

    // Returns background image
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    // Returns king's image
    private Image createKingImage() {
        kingTexture = kingsFeast.getAssetManager().get("kingplaceholder.png");
        Image king = new Image(kingTexture);
        king.setSize(kingTexture.getWidth(), kingTexture.getHeight());
        king.setPosition(kingTexture.getWidth() / 2 - 100, kingTexture.getHeight() / 3);
        return king;
    }

    // Returns king speech background texture
    private Image createKingSpeechBg() {
        kingSpeech = kingsFeast.getAssetManager().get("kingspeech.png");
        Image kingSpeechBubble = new Image(kingSpeech);
        kingSpeechBubble.setSize(kingSpeech.getWidth() - 50, kingSpeech.getHeight() - 50);
        kingSpeechBubble.setPosition(GAME_HEIGHT / 2, GAME_HEIGHT - kingSpeech.getHeight() / 2, Align.center);
        return kingSpeechBubble;
    }

    // Return image of a scroll
    private Image createScroll() {
        scroll = kingsFeast.getAssetManager().get("tausta.png");
        Image scrollBg = new Image(scroll);
        scrollBg.setSize(scroll.getWidth() - 180, scroll.getHeight() - 250);
        scrollBg.setPosition(GAME_WIDTH - scroll.getWidth() / 2 + 230, GAME_HEIGHT - scroll.getHeight() / 2 + 150, Align.center);
        return scrollBg;
    }

    // Return ok imagebutton
    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setPosition((GAME_WIDTH / 2) + (GAME_WIDTH / 4) - 160, GAME_HEIGHT / 6 - 50);
        ok.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                // Save game on level swap
                kingsFeast.saveGameOnLevelSwap();
                int tmp = Integer.parseInt(throwAmount);
                kingsFeast.getPrefs().putInteger("totalThrows",
                        kingsFeast.getPrefs().getInteger("totalThrows") + tmp);
                kingsFeast.getPrefs().flush();
                dispose();
                // Load pollution screen
                kingsFeast.setScreen(new PollutionScreen(kingsFeast));
            }
        });
        return ok;
    }

    // Initialize all fonts used in screen
    private void initFonts() {
        speechFont = new BitmapFont();
        negativeFont = new BitmapFont();
        positiveFont = new BitmapFont();

        // Font for showing game data (score etc.)
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = FONT_SIZE;
        fontParameter.borderWidth = 4;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        // Font for king's dialogue
        FreeTypeFontGenerator speechFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter speechFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        speechFontParameter.size = SPEECH_FONT_SIZE;
        speechFontParameter.color = Color.BLACK;
        speechFont = speechFontGenerator.generateFont(speechFontParameter);

        // Font for negative pollution change
        FreeTypeFontGenerator negativeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter negativeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        negativeFontParameter.size = FONT_SIZE;
        negativeFontParameter.color = Color.RED;
        negativeFont = negativeFontGenerator.generateFont(negativeFontParameter);

        // Font for positive pollution change
        FreeTypeFontGenerator positiveFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter positiveFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        positiveFontParameter.size = FONT_SIZE;
        positiveFontParameter.color = Color.OLIVE;
        positiveFont = positiveFontGenerator.generateFont(positiveFontParameter);
    }

    // Return pig lifeline imagebutton
    private ImageButton createPigsLifeline() {
        int totalScore = Integer.parseInt(kingsFeast.getTotalScore());

        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            // Load two textures for enabled and disabled states
            pigsTexture = kingsFeast.getAssetManager().get("pigsplaceholder.png");
            pigsDisabledTexture = kingsFeast.getAssetManager().get("pigsdisabledplaceholder.png");
        } else {
            // Load two textures for enabled and disabled states
            pigsTexture = kingsFeast.getAssetManager().get("siatenabled.png");
            pigsDisabledTexture = kingsFeast.getAssetManager().get("siatdisabled.png");
        }


        pigsLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(pigsTexture)),
                new TextureRegionDrawable(new TextureRegion(pigsTexture)),
                new TextureRegionDrawable(new TextureRegion(pigsDisabledTexture)));
        pigsLifeline.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH + 100, 400);
        pigsLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        // Check if button is enabled or disabled based on score
        if(totalScore >= 1000) {
            pigsLifeline.setChecked(false);
            isPigsUsed = false;
        } else {
            pigsLifeline.setChecked(true);
            pigsLifeline.setTouchable(Touchable.disabled);
            isPigsUsed = true;
        }

        // If lifeline is enabled and not used, add listener
        if(!isPigsUsed) {
            pigsLifeline.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    kingsFeast.setPollutionLevel(-7);
                    kingsFeast.setTotalScore(-1000);
                    pigsLifeline.setChecked(true);

                    checkLifelineEligibility("compost");
                    checkLifelineEligibility("poor");

                    // Disable button after used
                    pigsLifeline.setTouchable(Touchable.disabled);
                }
            });
        }
        return pigsLifeline;
    }

    // Return compost lifeline imagebutton
    private ImageButton createCompostLifeLine() {
        int totalScore  = Integer.parseInt(kingsFeast.getTotalScore());

        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            // Load two textures for enabled and disabled states
            compostTexture = kingsFeast.getAssetManager().get("compostplaceholder.png");
            compostDisabledTexture = kingsFeast.getAssetManager().get("compostdisabledplaceholder.png");
        } else {
            // Load two textures for enabled and disabled states
            compostTexture = kingsFeast.getAssetManager().get("kompostienabled.png");
            compostDisabledTexture = kingsFeast.getAssetManager().get("kompostidisabled.png");
        }

        compostLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(compostTexture)),
                new TextureRegionDrawable(new TextureRegion(compostTexture)),
                new TextureRegionDrawable(new TextureRegion(compostDisabledTexture)));
        compostLifeline.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH + 100, 250);
        compostLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        // Check if button is enabled or disabled based on score
        if(totalScore >= 2000) {
            compostLifeline.setChecked(false);
            isCompostUsed = false;
        } else {
            compostLifeline.setChecked(true);
            compostLifeline.setTouchable(Touchable.disabled);
            isCompostUsed = true;
        }

        // If lifeline is enabled and not used, add listener
        if(!isCompostUsed) {
            compostLifeline.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    kingsFeast.setPollutionLevel(-10);
                    kingsFeast.setTotalScore(-2000);
                    compostLifeline.setChecked(true);
                    checkLifelineEligibility("pigs");
                    checkLifelineEligibility("poor");

                    // Disable button after used
                    compostLifeline.setTouchable(Touchable.disabled);
                }
            });
        }
        return compostLifeline;
    }

    // Return poor lifeline imagebutton
    private ImageButton createPoorLifeLine() {
        int totalScore  = Integer.parseInt(kingsFeast.getTotalScore());

        // Checks what language is enabled and loads texture accordingly
        if(kingsFeast.isEnglishEnabled()) {
            // Load two textures for enabled and disabled states
            poorTexture = kingsFeast.getAssetManager().get("poorplaceholder.png");
            poorDisabledTexture = kingsFeast.getAssetManager().get("poordisabledplaceholder.png");
        } else {
            // Load two textures for enabled and disabled states
            poorTexture = kingsFeast.getAssetManager().get("koyhaenabled.png");
            poorDisabledTexture = kingsFeast.getAssetManager().get("koyhadisabled.png");
        }

        poorLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(poorTexture)),
                new TextureRegionDrawable(new TextureRegion(poorTexture)),
                new TextureRegionDrawable(new TextureRegion(poorDisabledTexture)));
        poorLifeline.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH + 100, 100);
        poorLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        // Check if button is enabled or disabled based on score
        if(totalScore >= 2500) {
            poorLifeline.setChecked(false);
            isPoorUsed = false;
        } else {
            poorLifeline.setChecked(true);
            poorLifeline.setTouchable(Touchable.disabled);
            isPoorUsed = true;
        }

        // If lifeline is enabled and not used, add listener
        if(!isPoorUsed) {
            poorLifeline.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    kingsFeast.setPollutionLevel(-15);
                    kingsFeast.setTotalScore(-2500);
                    poorLifeline.setChecked(true);
                    checkLifelineEligibility("compost");
                    checkLifelineEligibility("pigs");

                    // Disable button after used
                    poorLifeline.setTouchable(Touchable.disabled);
                }
            });
        }
        return poorLifeline;
    }

    // Method to draw text showing how pollution level has changed
    private void drawPollutionEffect() {
        int oldPol = kingsFeast.getOldPollution();
        int newPol = Integer.parseInt(kingsFeast.getPollutionLevel());

        int x;
        int y = 500;

        if(kingsFeast.isEnglishEnabled()) {
            x = 260;
        } else {
            x = 370;
        }

        // Check did pollution level decrease or increase and show correct text accordingly
        if(oldPol < newPol) {
            negativeFont.draw(batch, "+" + (newPol - oldPol), GAME_WIDTH - x, GAME_HEIGHT - y);
        } else if (newPol < oldPol) {
            positiveFont.draw(batch, "-" + (oldPol - newPol), GAME_WIDTH - x, GAME_HEIGHT - y);
        }
    }

    private void checkLifelineEligibility(String lifeLine) {
        int totalScore  = Integer.parseInt(kingsFeast.getTotalScore());

        switch(lifeLine) {
            case "pigs":
                if(totalScore < 1000) {
                    pigsLifeline.setChecked(true);
                    pigsLifeline.setTouchable(Touchable.disabled);
                }
                break;
            case "compost":
                if(totalScore < 2000 && kingsFeast.isStoryPointShown(2)) {
                    compostLifeline.setChecked(true);
                    compostLifeline.setTouchable(Touchable.disabled);
                }
                break;
            case "poor":
                if(totalScore < 2500 && kingsFeast.isStoryPointShown(4)) {
                    poorLifeline.setChecked(true);
                    poorLifeline.setTouchable(Touchable.disabled);
                }
                break;
        }
    }

    public void checkIfGameEnd() {
        if(pollutionCounter == 0) {
            kingsFeast.setScreen(new PollutionScreen(kingsFeast));
            dispose();
        }
    }

    private void checkStoryPoints() {
        int pollution = Integer.parseInt(kingsFeast.getPollutionLevel());
        int waste = Integer.parseInt(foodWasteAmount);

        if(pollution <= 80 && pollution > 70 && !kingsFeast.isStoryPointShown(1)) {
            story = myBundle.get("story1");
            kingsFeast.setStoryPointShown(1, true);
        } else if(pollution <= 70 && pollution > 60 && !kingsFeast.isStoryPointShown(2)) {
            story = myBundle.get("story2");
            kingsFeast.setStoryPointShown(2,true);
        } else if(pollution <= 60 && pollution > 50 && !kingsFeast.isStoryPointShown(3)) {
            story = myBundle.get("story3");
            kingsFeast.setStoryPointShown(3, true);
        } else if(pollution <= 50 && pollution > 40 && !kingsFeast.isStoryPointShown(4)) {
            story = myBundle.get("story4");
            kingsFeast.setStoryPointShown(4, true);
        } else if(pollution <= 40 && pollution > 30 && !kingsFeast.isStoryPointShown(5)) {
            story = myBundle.get("story5");
            kingsFeast.setStoryPointShown(5, true);
        } else if(pollution <= 30 && pollution > 20 && !kingsFeast.isStoryPointShown(6)) {
            story = myBundle.get("story6");
            kingsFeast.setStoryPointShown(6, true);
        } else if(pollution <= 20 && pollution > 0 && !kingsFeast.isStoryPointShown(7)) {
            story = myBundle.get("story7");
            kingsFeast.setStoryPointShown(7, true);
        } else {
            if(waste < 4) {
                story = myBundle.get("commentGreat");
            } else if (waste >= 4 && waste < 7) {
                story = myBundle.get("commentOk");
            } else if (waste >= 7) {
                story = myBundle.get("commentBad");
            }
        }
    }

    private void showStoryPoint() {
        speechFont.draw(batch, story, 35, GAME_HEIGHT - 65);
    }

    // Dispose stage and fonts
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        speechFont.dispose();
        negativeFont.dispose();
        positiveFont.dispose();
    }
}
