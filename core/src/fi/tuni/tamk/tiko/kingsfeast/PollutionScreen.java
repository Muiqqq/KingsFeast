package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * PollutionScreen class handles all stuff related to showing the player visually the pollution
 * level.
 */
public class PollutionScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;

    // Screen stuff
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
    private final float BUTTON_WIDTH = 500f;
    private final float BUTTON_HEIGHT = 120f;
    private final int FONT_SIZE = 48;
    private SpriteBatch batch;
    private Stage stage;
    private OrthographicCamera camera;

    // Textures and fonts
    private Texture okTexture;
    private Texture kingSpeech;
    private Texture backgroundTexture;
    private BitmapFont font;

    // Localization
    I18NBundle myBundle;

    // Game logic stuff
    private boolean gameWon;
    private boolean gameLost;

    /**
     * Constructor takes in the game object to access its methods.
     * @param kingsFeast to access its methods.
     */
    public PollutionScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
        batch = kingsFeast.getSpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        // Get and set the language to be used in the level
        myBundle = kingsFeast.langManager.getCurrentBundle();

        // Initialize booleans to false to enable checking game end
        gameWon = false;
        gameLost = false;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        font = Util.initFont(FONT_SIZE);

        // Add actors to the stage
        stage.addActor(createBackgroundImage());

        // Check if game has ended and if so initiate game end
        checkGameEnd();
        if(gameWon || gameLost) {
            stage.addActor(createOkButton());
        }
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

        // Depending on game state draw text on the screen
        if(gameWon) {
            font.draw(batch, myBundle.get("gameWonRiver"), GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 250);
            font.draw(batch, myBundle.get("gameWon")+ ": " + kingsFeast.getTotalScore(), GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 100);
        } else if (gameLost) {
            font.draw(batch, myBundle.get("gameLost")+ ": " + kingsFeast.getTotalScore(), GAME_WIDTH / 5 + 130, GAME_HEIGHT / 2 + 250);
        } else {
            font.draw(batch, myBundle.get("pollutionLevel")+ ": " + kingsFeast.getPollutionLevel() + "/100", GAME_WIDTH / 3 - 20, GAME_HEIGHT - BUTTON_HEIGHT * 2);
        }
        batch.end();

        // If game has not ended, continue game by tapping
        if(Gdx.input.isTouched() && !gameWon && !gameLost) {
            kingsFeast.setScreen(new GameScreen(kingsFeast));
            dispose();
        }
    }

    /**
     * Creates a background image for the screen.
     * @return Background image.
     */
    private Image createBackgroundImage() {
        getPollutionBackground();
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    /**
     * Returns an ok button to the screen. If game is won or lost tapping the button returns player
     * to main menu and clears the save file for a new game.
     * @return ok Imagebutton.
     */
    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ok.setPosition(GAME_WIDTH / 2 - BUTTON_WIDTH / 2, BUTTON_HEIGHT);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                // If game is won or lost clear game data for a new game and go back to main menu
                if(gameWon || gameLost) {
                    kingsFeast.clearSaveState();
                    kingsFeast.setScreen(new MainMenuScreen(kingsFeast));
                    dispose();
                }
            }
        });
        return ok;
    }

    /**
     * Gets correct river background depending on pollution level.
     */
    private void getPollutionBackground() {
        int pollution = Integer.parseInt(kingsFeast.getPollutionLevel());

        if(pollution == 0) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä0%.png");
        } else if (pollution > 0 && pollution < 20) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä10%.jpg");
        } else if (pollution >= 20 && pollution < 30) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä20%.jpg");
        } else if (pollution >= 30 && pollution < 40) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä30%.jpg");
        } else if (pollution >= 40 && pollution < 50) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä40%.jpg");
        } else if (pollution >= 50 && pollution < 70) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä50%.jpg");
        } else if (pollution >= 70 && pollution < 90) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä60%.jpg");
        } else if (pollution >= 90 && pollution <= 100) {
            backgroundTexture =  kingsFeast.getAssetManager().get("Jokinäkymä70%.jpg");
        }

    }

    /**
     * Checks if game has ended (pollution 0 or 100). If so, initiate correct screen.
     */
    private void checkGameEnd() {
        int pollution = Integer.parseInt(kingsFeast.getPollutionLevel());
        if(pollution >= 100) {
            gameLost();
        } else if (pollution <= 0) {
            gameWon();
        }
    }

    /**
     * Shows game lost screen.
     */
    private void gameLost() {
        stage.addActor(createGameEndBg());
        stage.addActor(createOkButton());
        gameLost = true;
    }

    /**
     * Shows game won screen.
     */
    private void gameWon() {
        stage.addActor(createGameEndBg());
        stage.addActor(createOkButton());
        gameWon = true;
    }

    /**
     * Creates a background scroll for game end text.
     * @return Background image for game end text.
     */
    private Image createGameEndBg() {
        kingSpeech = kingsFeast.getAssetManager().get("taustavaaka.png");
        Image gameEndBg = new Image(kingSpeech);
        gameEndBg.setSize(kingSpeech.getWidth(), kingSpeech.getHeight() - 50);
        gameEndBg.setPosition(GAME_WIDTH / 2 - kingSpeech.getWidth() / 2 + 50, GAME_HEIGHT / 2 - kingSpeech.getHeight() / 2 + 50);
        return gameEndBg;
    }

    /**
     * Disposes elements after screen change.
     */
    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
