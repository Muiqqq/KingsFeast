package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * All main gameplay stuff happens in this screen.
 * This is the previous main class.
 */
public class GameScreen extends ScreenAdapter {
    // TODO: Add documentation everywhere.
    // TODO: Make camera follow the foodPlate, within a levels bounds.

    private final boolean DEBUG_PHYSICS = true;

    // Initial values, work in progress. Pixels -> meters.
    private final float unitScale = 1 / 100f;
    private final float GAME_WIDTH = 800 * unitScale;
    private final float GAME_HEIGHT = 480 * unitScale;
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
    private Vector3 touchPos;
    private Rectangle firingBounds;
    private boolean canThrow;

    // TODO: rename shit and refactor shit to a better form

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

        tiledMap = new TmxMapLoader().load("pantestmap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        touchPos = new Vector3();

        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        worldContactListener = new WorldContactListener();
        world.setContactListener(worldContactListener);

        inputProcessing();
        foodPlate = new FoodPlate(unitScale);

        // bounds should be set to something representing the object being flung from the sling
        // eventually.
        firingBounds = new Rectangle(0, 0, 128, 128);
        canThrow = false;
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        Util.clearScreen();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.begin();
        batch.end();

        drawDebug();
        update();
        Util.worldStep(world, delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void update() {
        enableThrowing();
        camera.update();
    }

    private void inputProcessing() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            Vector3 lastTouch = new Vector3();
            Vector3 tmp = new Vector3();

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                lastTouch.set(screenX, screenY, 0);
                camera.unproject(lastTouch);
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                handleThrowCalculations(screenX, screenY);
                handleCameraPanning(tmp, lastTouch, screenX);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // Currently the physics object gets created when the user lets go of
                // mouse button, or stops touching the screen. Could be done differently.
                handleThrowing();
                return true;
            }
        });
    }

    private void drawDebug() {
        if (DEBUG_PHYSICS) {
            box2DDebugRenderer.render(world, camera.combined);
            // Following code draws the rectangles and the line you see when testing.
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.rect(Util.convertPixelsToMetres(foodPlate.anchor.x - 5, unitScale),
                    Util.convertPixelsToMetres(foodPlate.anchor.y - 5, unitScale),
                    Util.convertPixelsToMetres(10, unitScale),
                    Util.convertPixelsToMetres(10, unitScale));

            shapeRenderer.rect(Util.convertPixelsToMetres(foodPlate.firingPos.x - 5, unitScale),
                    Util.convertPixelsToMetres(foodPlate.firingPos.y - 5, unitScale),
                    Util.convertPixelsToMetres(10, unitScale),
                    Util.convertPixelsToMetres(10, unitScale));

            shapeRenderer.line(Util.convertPixelsToMetres(foodPlate.anchor.x, unitScale),
                    Util.convertPixelsToMetres(foodPlate.anchor.y, unitScale),
                    Util.convertPixelsToMetres(foodPlate.firingPos.x, unitScale),
                    Util.convertPixelsToMetres(foodPlate.firingPos.y, unitScale));

            shapeRenderer.end();
        }
    }

    private void enableThrowing() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if (Gdx.input.justTouched()) {
            if (firingBounds.contains(touchPos.x / unitScale, touchPos.y / unitScale)) {
                canThrow = true;

                System.out.println("touchPosX: " + touchPos.x / unitScale);
                System.out.println("touchPosY: " + touchPos.y / unitScale);
            }
        }
    }

    // put these in util maybe? might not work because of camera stuff.
    private void handleCameraPanning(Vector3 tmp, Vector3 lastTouch, int screenX) {
        if (!firingBounds.contains(touchPos.x / unitScale, touchPos.y / unitScale) &&
                !canThrow) {

            tmp.set(screenX, 0, 0);
            camera.unproject(tmp);
            tmp.sub(lastTouch).scl(-0.1f, 0, 0);
            camera.translate(tmp);
        }
    }

    private void handleThrowCalculations(int screenX, int screenY) {
        if (canThrow) {
            // transforms screen coordinates to game coordinates.
            // InputProcessor returns values with x = 0, y = 0 in top left,
            // so they have to be transformed.
            Vector3 screenCoordinates = new Vector3(screenX, screenY, 0);
            camera.unproject(screenCoordinates);

            // calculates throw based on the dragging.
            foodPlate.calculateAngleAndDistance(screenCoordinates.x,
                    screenCoordinates.y,
                    unitScale);
        }
    }

    private void handleThrowing() {
        if (canThrow) {
            foodPlate.createBody(world, unitScale);

            // This just resets the firing position back to the anchor.
            foodPlate.firingPos.set(foodPlate.anchor.cpy());
            canThrow = false;
        }
    }
}
