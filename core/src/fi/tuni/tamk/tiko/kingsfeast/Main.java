package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    private ShapeRenderer shapeRenderer;
    private WorldContactListener worldContactListener;
    private FoodPlate foodPlate;
    @Override
    public void create () {
        batch = new SpriteBatch();
        world = new World(gravity, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        // tiledMap
        // tiledMapRenderer

        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        worldContactListener = new WorldContactListener();
        world.setContactListener(worldContactListener);

        inputProcessing();
        foodPlate = new FoodPlate(unitScale);
    }

    @Override
    public void render () {
        batch.setProjectionMatrix(camera.combined);
        Util.clearScreen();

        batch.begin();
        batch.end();

        if(DEBUG_PHYSICS) {
            drawDebug();
        }

        Util.worldStep(world, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    // Input handling goes here. Remember to document what goes in these methods for future
    // reference.
    private void inputProcessing() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                // transforms screen coordinates to game coords.
                Vector3 screenCoordinates = new Vector3(screenX, screenY, 0);
                camera.unproject(screenCoordinates);

                // calculates throw
                foodPlate.calculateAngleAndDistance(screenCoordinates.x,
                        screenCoordinates.y,
                        unitScale);

                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                foodPlate.createBody(world, unitScale);
                foodPlate.firingPos.set(foodPlate.anchor.cpy());
                return true;
            }
        });
    }

    private void drawDebug() {
        box2DDebugRenderer.render(world, camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.rect(foodPlate.anchor.x - 5,
                foodPlate.anchor.y - 5,
                10,
                10);

        shapeRenderer.rect(foodPlate.firingPos.x - 5,
                foodPlate.firingPos.y - 5,
                10,
                10);

        shapeRenderer.line(foodPlate.anchor.x,
                foodPlate.anchor.y,
                foodPlate.firingPos.x,
                foodPlate.firingPos.y);

        shapeRenderer.end();
    }
}
