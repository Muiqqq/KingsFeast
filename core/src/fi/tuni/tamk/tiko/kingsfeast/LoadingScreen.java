package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private static final float PROGRESS_BAR_WIDTH = 100;
    private static final float PROGRESS_BAR_HEIGHT = 25;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;

    private float progress;

    LoadingScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
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
        kingsFeast.setLevels(buildLevels());
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
        kingsFeast.getAssetManager().load("map2.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map3.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map4.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map5.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map6.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("map7.tmx", TiledMap.class);
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



        kingsFeast.getAssetManager().load("1.mp3", Music.class);

        // don't forget this after loading
        kingsFeast.getAssetManager().finishLoading();
    }

    private Array<LevelData> buildLevels() {
        Array<LevelData> levels = new Array<>();
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

        return levels;
    }
}
