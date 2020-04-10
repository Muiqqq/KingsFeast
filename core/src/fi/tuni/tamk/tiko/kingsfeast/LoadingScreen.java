package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import sun.applet.Main;


/**
 * TODO: DOCUMENTATION!
 *  - Add graphics maybe? Background, progress bar etc.
 *  - Dunno if this will ever be visible for long enough for that to matter.
 *  - Could use this as a splash screen?
 *
 * This is a loading screen.
 */
public class LoadingScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private final LevelBuilder levelBuilder;

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private static final float PROGRESS_BAR_WIDTH = 100;
    private static final float PROGRESS_BAR_HEIGHT = 25;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;

    private float progress;

    LoadingScreen(KingsFeast kingsFeast, LevelBuilder levelBuilder) {
        this.kingsFeast = kingsFeast;
        this.levelBuilder = levelBuilder;
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
        viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        loadAssets();
        kingsFeast.setLevels(levelBuilder.buildLevels());
    }

    @Override
    public void render(float delta) {
        update();
        Util.clearScreen();
        draw();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private void update() {
        if (kingsFeast.getAssetManager().update()) {
            kingsFeast.setScreen(new MainMenuScreen(kingsFeast));
        }
        progress = kingsFeast.getAssetManager().getProgress();
    }

    // All of this just draws the progress bar.
    private void draw() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
                (GAME_WIDTH - PROGRESS_BAR_WIDTH) / 2, (GAME_HEIGHT - PROGRESS_BAR_HEIGHT) / 2,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }

    // All the assets should be loaded here.
    private void loadAssets() {
        kingsFeast.getAssetManager().getLogger().setLevel(Logger.DEBUG);
        kingsFeast.getAssetManager().load("map1.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level1.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level2.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level3.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level4.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("redfood.png", Texture.class);
        kingsFeast.getAssetManager().load("bluefood.png", Texture.class);
        kingsFeast.getAssetManager().load("greenfood.png", Texture.class);
        kingsFeast.getAssetManager().load("skipButton-up.png", Texture.class);
        kingsFeast.getAssetManager().load("skipButton-down.png", Texture.class);
        kingsFeast.getAssetManager().load("mainmenubackgroundtitle.jpg", Texture.class);
        kingsFeast.getAssetManager().load("StartGameButton.png", Texture.class);
        kingsFeast.getAssetManager().load("SettingsButton.png", Texture.class);
        kingsFeast.getAssetManager().load("riverscreen.png", Texture.class);
        kingsFeast.getAssetManager().load("credits.png", Texture.class);
        kingsFeast.getAssetManager().load("OkButton.png", Texture.class);
        kingsFeast.getAssetManager().load("MusicOnButton.png", Texture.class);
        kingsFeast.getAssetManager().load("MusicOffButton.png", Texture.class);
        kingsFeast.getAssetManager().load("SoundOnButton.png", Texture.class);
        kingsFeast.getAssetManager().load("SoundOffButton.png", Texture.class);
        kingsFeast.getAssetManager().load("credits_bg.png", Texture.class);
        kingsFeast.getAssetManager().load("options.jpg", Texture.class);
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
        kingsFeast.getAssetManager().load("tekstitausta.png", Texture.class);
        kingsFeast.getAssetManager().load("tekstitaustahorizontal.png", Texture.class);

        kingsFeast.getAssetManager().load("1.mp3", Music.class);

        // don't forget this after loading
        kingsFeast.getAssetManager().finishLoading();
    }
}
