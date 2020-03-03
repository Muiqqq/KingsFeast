package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen extends ScreenAdapter {
    // TODO: Add documentation everywhere.
    // TODO: Make camera follow the foodPlate, within a levels bounds.

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

    /**
     * Screens use show() instead of create()
     *
     * They are pretty much the same thing.
     */
    @Override
    public void show() {
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
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        Util.clearScreen();

        batch.begin();
        batch.end();

        if (DEBUG_PHYSICS) {
            drawDebug();
        }

        Util.worldStep(world, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void inputProcessing() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                // transforms screen coordinates to game coordinates.
                // InputProcessor returns values with x = 0, y = 0 in top left,
                // so they have to be transformed.
                Vector3 screenCoordinates = new Vector3(screenX, screenY, 0);
                camera.unproject(screenCoordinates);

                // calculates throw based on the dragging.
                foodPlate.calculateAngleAndDistance(screenCoordinates.x,
                        screenCoordinates.y,
                        unitScale);

                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // Currently the physics object gets created when the user lets go of
                // mouse button, or stops touching the screen. Could be done differently.
                foodPlate.createBody(world, unitScale);

                // This just resets the firing position back to the anchor.
                foodPlate.firingPos.set(foodPlate.anchor.cpy());
                return true;
            }
        });
    }

    private void drawDebug() {
        box2DDebugRenderer.render(world, camera.combined);

        // Following code draws the rectangles and the line you see when testing.
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
