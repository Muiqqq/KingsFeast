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
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

/**
 * All main gameplay stuff happens in this screen.
 * This is the previous main class.
 */
public class GameScreen extends ScreenAdapter {
    // TODO: Add documentation everywhere
    //  -Move throwing related methods to FoodPlate class if possible
    //  -Contact handling should happen in case specific methods

    private final boolean DEBUG_PHYSICS = true;

    private final float unitScale = Util.getUnitScale();
    // Initial values, work in progress. Pixels -> meters.
    private final float GAME_WIDTH = 928 * unitScale;
    private final float GAME_HEIGHT = 544 * unitScale;

    private final Vector2 gravity = new Vector2(0, -9.8f);

    // levels class will contain variables and constants for different levels.
    private float LEVEL_WIDTH;

    private SpriteBatch batch;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private ShapeRenderer shapeRenderer;
    private FoodPlate foodPlate;
    private Vector3 touchPos;
    private Rectangle firingBounds;

    // put this in FoodPlate too if possible
    private boolean canThrow;
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

        tiledMap = new TmxMapLoader().load("cameratestmap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        LEVEL_WIDTH = Util.getLevelWidth(tiledMap) * unitScale;

        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        BodyBuilder.transformObjectsToBodies(tiledMap, world,
                "collision", "walls", unitScale);

        BodyBuilder.transformObjectsToBodies(tiledMap, world,
                "goal", "goal", unitScale);

        contactProcessing();
        inputProcessing();
        foodPlate = new FoodPlate();

        touchPos = new Vector3();

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
        foodPlate.destroyBody(world, this);
        foodPlate.checkIfBodyStopped();
        snapCameraToBody();
        handleCameraLimits();

        // all camera methods have to be before camera.update();
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

    private void contactProcessing() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                String userDataA = (String) contact.getFixtureA().getBody().getUserData();
                String userDataB = (String) contact.getFixtureB().getBody().getUserData();

                // if foodplate collided with goal -> destroy it's body
                if (userDataA.equals("foodPlate") && userDataB.equals("goal") ||
                        userDataB.equals("foodPlate") && userDataA.equals("goal")) {

                    foodPlate.removeBody = true;
                    foodPlate.isPlateFlying = false;
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    private void drawDebug() {
        if (DEBUG_PHYSICS) {
            box2DDebugRenderer.render(world, camera.combined);
            // Following code draws the rectangles and the line you see when testing.
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.rect(Util.convertPixelsToMetres(foodPlate.anchor.x - 5),
                    Util.convertPixelsToMetres(foodPlate.anchor.y - 5),
                    Util.convertPixelsToMetres(10),
                    Util.convertPixelsToMetres(10));

            shapeRenderer.rect(Util.convertPixelsToMetres(foodPlate.firingPos.x - 5),
                    Util.convertPixelsToMetres(foodPlate.firingPos.y - 5),
                    Util.convertPixelsToMetres(10),
                    Util.convertPixelsToMetres(10));

            shapeRenderer.line(Util.convertPixelsToMetres(foodPlate.anchor.x),
                    Util.convertPixelsToMetres(foodPlate.anchor.y),
                    Util.convertPixelsToMetres(foodPlate.firingPos.x),
                    Util.convertPixelsToMetres(foodPlate.firingPos.y));

            shapeRenderer.end();
        }
    }

    // put camera stuff in util maybe? might not work
    private void handleCameraLimits() {
        if (camera.position.x - (camera.viewportWidth / 2) <= 0)  {
            camera.position.x = 0 + camera.viewportWidth / 2;
        }

        if (camera.position.x + (camera.viewportWidth / 2) >= LEVEL_WIDTH) {
            camera.position.x = LEVEL_WIDTH - (camera.viewportWidth / 2);
        }
    }

    private void snapCameraToBody() {
        if (foodPlate.isPlateFlying) {
            camera.position.x = foodPlate.body.getWorldCenter().x;
        }
    }

    void cameraReset() {
        camera.position.x = 0 + (camera.viewportWidth / 2);
    }

    private void handleCameraPanning(Vector3 tmp, Vector3 lastTouch, int screenX) {
        float posX = Util.convertMetresToPixels(touchPos.x);
        float posY = Util.convertMetresToPixels(touchPos.y);
        if (!foodPlate.isPlateFlying) {
            if (!firingBounds.contains(posX, posY) && !canThrow) {
                tmp.set(screenX, 0, 0);
                camera.unproject(tmp);
                tmp.sub(lastTouch).scl(-0.1f, 0, 0);
                camera.translate(tmp);
            }
        }
    }

    // these might belong in FoodPlate?
    private void enableThrowing() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        float posX = Util.convertMetresToPixels(touchPos.x);
        float posY = Util.convertMetresToPixels(touchPos.y);

        if (Gdx.input.justTouched()) {
            if (firingBounds.contains(posX, posY)) {
                canThrow = true;

                System.out.println("touchPosX: " + touchPos.x / unitScale);
                System.out.println("touchPosY: " + touchPos.y / unitScale);
            }
        }
    }

    private void handleThrowCalculations(int screenX, int screenY) {
        if (canThrow && !foodPlate.isPlateFlying) {
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
        if (canThrow && !foodPlate.isPlateFlying) {
            foodPlate.body = foodPlate.createBody(world);

            // This just resets the firing position back to the anchor.
            foodPlate.firingPos.set(foodPlate.anchor.cpy());
            canThrow = false;
        }
    }
}
