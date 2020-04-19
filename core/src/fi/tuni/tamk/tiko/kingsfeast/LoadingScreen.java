package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This class is used to display a loading screen to the user whilst assets are loaded.
 *
 * A simple screen which renders a background image and an animation. Assets are loaded
 * with AssetManager asynchronously. Moves on to the next screen after loading is done,
 * in this case to the main menu screen.
 */
public class LoadingScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private SpriteBatch sb;

    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Texture background;

    private Animation<TextureRegion> loadingAnimation;
    private Texture loadingSheet;
    private TextureRegion currentFrame;
    private float stateTime = 0.0f;

    /**
     * Constructor prepares the animation and background image.
     *
     * @param kingsFeast Game object, used to get access to its and its parent's methods.
     */
    LoadingScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
        sb = kingsFeast.getSpriteBatch();

        background = new Texture("mainmenubackgroundtitle.jpg");
        loadingSheet = getLocalizedLoadingTexture();
        createAnimation();
        currentFrame = loadingAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(GAME_WIDTH / 2, GAME_HEIGHT / 2, 0);
        camera.update();
        viewport = new StretchViewport(GAME_WIDTH, GAME_HEIGHT, camera);
        loadAssets();
    }

    @Override
    public void render(float delta) {
        sb.setProjectionMatrix(camera.combined);
        update();
        Util.clearScreen();
        sb.begin();
        draw();
        sb.end();
    }

    @Override
    public void dispose() {
        loadingSheet.dispose();
        background.dispose();
    }

    /**
     * Actions that happen once loading is finished.
     */
    private void update() {
        if (kingsFeast.getAssetManager().update()) {
            kingsFeast.getAssetManager().finishLoading();
            kingsFeast.setLevels(buildLevels());
            kingsFeast.setMusic();
            kingsFeast.setSounds();
            dispose();
            kingsFeast.setScreen(new FeedbackScreen(kingsFeast, 2, 2));
        }
    }

    /**
     * Draws the background for loading screen and the animation.
     */
    private void draw() {
        sb.draw(background, 0, 0, GAME_WIDTH, GAME_HEIGHT);
        drawAnimation(sb);
    }

    /**
     * Loads all the assets. A huge and ugly list of assets to be loaded.
     *
     * Note to self: use a texture atlas next time.
     */
    private void loadAssets() {
        kingsFeast.getAssetManager().getLogger().setLevel(Logger.DEBUG);
        kingsFeast.getAssetManager().load("map1.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map2.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map3.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map4.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map5.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map6.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map7.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map8.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map9.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map10.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map11.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map12.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map13.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level4.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("redfood.png", Texture.class);
        kingsFeast.getAssetManager().load("bluefood.png", Texture.class);
        kingsFeast.getAssetManager().load("greenfood.png", Texture.class);
        kingsFeast.getAssetManager().load("skipButton-up.png", Texture.class);
        kingsFeast.getAssetManager().load("skipButton-down.png", Texture.class);
        kingsFeast.getAssetManager().load("mainmenubackgroundtitle.jpg", Texture.class);
        kingsFeast.getAssetManager().load("SettingsButton.png", Texture.class);
        kingsFeast.getAssetManager().load("riverscreen.png", Texture.class);
        kingsFeast.getAssetManager().load("OkButton.png", Texture.class);
        kingsFeast.getAssetManager().load("MusicOnButton.png", Texture.class);
        kingsFeast.getAssetManager().load("MusicOffButton.png", Texture.class);
        kingsFeast.getAssetManager().load("SoundOnButton.png", Texture.class);
        kingsFeast.getAssetManager().load("SoundOffButton.png", Texture.class);
        kingsFeast.getAssetManager().load("pigsplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("compostplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("poorplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("HowToPlayButton.png", Texture.class);
        kingsFeast.getAssetManager().load("ContinueButton.png", Texture.class);
        kingsFeast.getAssetManager().load("MainMenuButton.png", Texture.class);
        kingsFeast.getAssetManager().load("LanguageEnButton.png", Texture.class);
        kingsFeast.getAssetManager().load("LanguageFiButton.png", Texture.class);
        kingsFeast.getAssetManager().load("NewGameButton.png", Texture.class);
        kingsFeast.getAssetManager().load("fruitSalad.png", Texture.class);
        kingsFeast.getAssetManager().load("compostdisabledplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("pigsdisabledplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("poordisabledplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("kingplaceholder.png", Texture.class);
        kingsFeast.getAssetManager().load("kingspeech.png", Texture.class);
        kingsFeast.getAssetManager().load("tausta.png", Texture.class);
        kingsFeast.getAssetManager().load("taustavaaka.png", Texture.class);
        kingsFeast.getAssetManager().load("aanetpaalla.png", Texture.class);
        kingsFeast.getAssetManager().load("aanetpois.png", Texture.class);
        kingsFeast.getAssetManager().load("asetukset.png", Texture.class);
        kingsFeast.getAssetManager().load("jatkapelia.png", Texture.class);
        kingsFeast.getAssetManager().load("kielienglanti.png", Texture.class);
        kingsFeast.getAssetManager().load("kielisuomi.png", Texture.class);
        kingsFeast.getAssetManager().load("musiikkipaalla.png", Texture.class);
        kingsFeast.getAssetManager().load("musiikkipois.png", Texture.class);
        kingsFeast.getAssetManager().load("uusipeli.png", Texture.class);
        kingsFeast.getAssetManager().load("kuinkapelata.png", Texture.class);
        kingsFeast.getAssetManager().load("siatenabled.png", Texture.class);
        kingsFeast.getAssetManager().load("siatdisabled.png", Texture.class);
        kingsFeast.getAssetManager().load("kompostienabled.png", Texture.class);
        kingsFeast.getAssetManager().load("kompostidisabled.png", Texture.class);
        kingsFeast.getAssetManager().load("koyhaenabled.png", Texture.class);
        kingsFeast.getAssetManager().load("koyhadisabled.png", Texture.class);
        kingsFeast.getAssetManager().load("tyhjanappi.png", Texture.class);
        kingsFeast.getAssetManager().load("howtoplaybg.png", Texture.class);

        kingsFeast.getAssetManager().load("1.mp3", Music.class);
    }

    /**
     * Creates an array containing all the levels in the game.
     *
     * @return an array containing all the levels of the game.
     */
    private Array<LevelData> buildLevels() {
        Array<LevelData> levels = new Array<>();
        int mapNumber = 1;
        for (int i = 0; i < 13; i++) {
            String mapFileName =  "map" + mapNumber + ".tmx";
            mapNumber++;
            levels.add(new LevelData(kingsFeast.getAssetManager().get(mapFileName,
                    TiledMap.class)));
        }

        /*
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map1.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map2.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map3.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map4.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map5.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map6.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map7.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map8.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map9.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map10.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map11.tmx",
                TiledMap.class)));
        levels.add(new LevelData(kingsFeast.getAssetManager().get("map12.tmx",
                TiledMap.class)));
        */
        return levels;
    }

    /**
     * Checks which language is being used and gets correct texture for loading animation.
     *
     * @return localized texture file used in the loading animation
     */
    private Texture getLocalizedLoadingTexture() {
        if(kingsFeast.isEnglishEnabled()) {
            return new Texture("loading_en.png");
        } else {
            return new Texture("loading_fi.png");
        }
    }

    /**
     * This creates the animation used in this screen.
     */
    private void createAnimation() {
        TextureRegion[][] tmp;
        TextureRegion[] frames;
        int FRAME_ROWS = 1;
        int FRAME_COLS = 5;

        tmp = TextureRegion.split(loadingSheet,
                loadingSheet.getWidth() / FRAME_COLS,
                loadingSheet.getHeight() / FRAME_ROWS);

        frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;

        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        loadingAnimation = new Animation<>( 12 / 60f, frames);
    }

    /**
     * Method used to draw the animation frame by frame.
     *
     * @param sb SpriteBatch, required to draw.
     */
    private void drawAnimation(SpriteBatch sb) {
        stateTime += Gdx.graphics.getDeltaTime();
        sb.draw(currentFrame,
                GAME_WIDTH / 2 - currentFrame.getRegionWidth(),
                GAME_HEIGHT / 6,
                currentFrame.getRegionWidth() * 2f,
                currentFrame.getRegionHeight() * 2f);

        currentFrame = loadingAnimation.getKeyFrame(stateTime, true);
    }
}
