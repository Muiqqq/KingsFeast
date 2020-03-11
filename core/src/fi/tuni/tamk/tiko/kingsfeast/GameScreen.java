package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
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
import com.badlogic.gdx.utils.Array;

/**
 * All main gameplay stuff happens in this screen.
 * This is the previous main class.
 */
public class GameScreen extends ScreenAdapter {
    // TODO: Add documentation everywhere
    //  -Move throwing related methods to FoodPlate class if possible
    //  -Contact handling should happen in case specific methods
    private final KingsFeast kingsFeast;

    private final boolean DEBUG_PHYSICS = true;

    private final float unitScale = Util.getUnitScale();
    // Initial values, work in progress. Pixels -> meters.
    private final float GAME_WIDTH = 928 * unitScale;
    private final float GAME_HEIGHT = 544 * unitScale;

    private final Vector2 gravity = new Vector2(0, -9.8f);

    private LevelData levelData;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private ShapeRenderer shapeRenderer;
    private FoodPlate foodPlate;
    private Vector3 touchPos;
    private Rectangle throwBounds;

    // put this in FoodPlate too if possible
    private boolean canThrow;
    private boolean wasTouchDragged = false;

    GameScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }
    /**
     * Screens use show() instead of create()
     *
     * They are pretty much the same thing.
     */
    @Override
    public void show() {
        levelData = kingsFeast.getLevels().get(kingsFeast.getCurrentLevel());
        batch = new SpriteBatch();
        world = new World(gravity, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        tiledMap = levelData.getTiledMap();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        BodyBuilder.transformObjectsToBodies(tiledMap, world,
                "collision", "walls");

        BodyBuilder.transformObjectsToBodies(tiledMap, world,
                "goal", "goal");

        contactProcessing();
        inputProcessing();
        foodPlate = new FoodPlate(levelData.getSlingAnchorPos(), levelData.getFoodTextures());
        touchPos = new Vector3();

        // bounds should be set to something representing the object being flung from the sling
        // eventually.
        throwBounds = levelData.getTHROW_BOUNDS();
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
        swapLevel();

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

        if (camera.position.x + (camera.viewportWidth / 2) >= levelData.getLEVEL_WIDTH()) {
            camera.position.x = levelData.getLEVEL_WIDTH() - (camera.viewportWidth / 2);
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
            if (!throwBounds.contains(posX, posY) && !canThrow) {
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
            if (throwBounds.contains(posX, posY)) {
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

            wasTouchDragged = true;
        }
    }

    private void handleThrowing() {
        if (canThrow && !foodPlate.isPlateFlying && wasTouchDragged) {
            foodPlate.body = foodPlate.createBody(world);

            // This just resets the firing position back to the anchor.
            foodPlate.firingPos.set(foodPlate.anchor.cpy());
            canThrow = false;
            wasTouchDragged = false;
        }
    }

    // swaps the level to the next one if current one is finished (all objects have been thrown)
    // currently just loops back to beginning.
    private void swapLevel() {
        if (foodPlate.allPlatesThrown) {
            if (kingsFeast.getCurrentLevel() < kingsFeast.getLevels().size - 1) {
                kingsFeast.incrementCurrentLevel();
                kingsFeast.setScreen(new GameScreen(kingsFeast));
            } else {
                kingsFeast.setCurrentLevel(0);
                kingsFeast.setScreen(new GameScreen(kingsFeast));
            }
        }
    }
}
