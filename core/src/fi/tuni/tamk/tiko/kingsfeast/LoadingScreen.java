package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
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
        kingsFeast.getAssetManager().load("level1.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level2.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level3.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("level4.tmx", TiledMap.class);
        kingsFeast.getAssetManager().load("redfood.png", Texture.class);
        kingsFeast.getAssetManager().load("bluefood.png", Texture.class);
        kingsFeast.getAssetManager().load("greenfood.png", Texture.class);
        kingsFeast.getAssetManager().load("redgoal.png", Texture.class);
        kingsFeast.getAssetManager().load("bluegoal.png", Texture.class);
        kingsFeast.getAssetManager().load("greengoal.png", Texture.class);
        kingsFeast.getAssetManager().load("skipButton-up.png", Texture.class);
        kingsFeast.getAssetManager().load("skipButton-down.png", Texture.class);

        // don't forget this after loading
        kingsFeast.getAssetManager().finishLoading();
    }
}
