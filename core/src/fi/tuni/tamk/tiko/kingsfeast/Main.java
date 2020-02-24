package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Main extends ApplicationAdapter {
    // TODO: Add documentation everywhere.

    private final boolean DEBUG_PHYSICS = true;

    // Initial values, work in progress. Pixels -> meters.
    private final float unitScale = 1 / 100f;
    private final float GAME_WIDTH = 800 * unitScale;
    private final float GAME_HEIGHT = 450 * unitScale;
    private final Vector2 gravity = new Vector2(0, -9.8f);

    private SpriteBatch batch;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private WorldContactListener worldContactListener;

    @Override
    public void create() {
        batch = new SpriteBatch();
        world = new World(gravity, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        tiledMap = new TmxMapLoader().load("transformtestmap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        BodyBuilder.transformObjectsToBodies(tiledMap,
                world,
                "ground-collision",
                "floor",
                unitScale);

        BodyBuilder.transformObjectsToBodies(tiledMap,
                world,
                "wall-collision",
                "wall",
                unitScale);

        BodyBuilder.transformObjectsToBodies(tiledMap,
                world,
                "death-layer",
                "spikes",
                unitScale);

        box2DDebugRenderer = new Box2DDebugRenderer();

        worldContactListener = new WorldContactListener();
        world.setContactListener(worldContactListener);
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        Util.clearScreen();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        moveCamera();
        camera.update();

        batch.begin();
        batch.end();

        if (DEBUG_PHYSICS) {
            box2DDebugRenderer.render(world, camera.combined);
        }

        world.step(1 / 60f, 8, 3);
    }

    public void moveCamera() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0.0f, 0.4f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0.0f, -0.4f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(0.4f, 0.0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-0.4f, 0.0f);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
