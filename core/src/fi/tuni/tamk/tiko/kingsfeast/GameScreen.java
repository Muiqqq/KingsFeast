package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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

import static com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.Color;

/**
 * All main gameplay magic happens in a GameScreen.
 *
 * Contains everything necessary for the physics-based throwing mechanic, levels, camera handling,
 * input handling, contact handling, etc.
 */
public class GameScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;

    private final boolean DEBUG_PHYSICS = false;

    private final float unitScale = Util.getUnitScale();
    private final float GAME_WIDTH = 1280 * unitScale;
    private final float GAME_HEIGHT = 736 * unitScale;

    private final Vector2 gravity = new Vector2(0, -8.8f);

    private LevelData levelData;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private ShapeRenderer shapeRenderer;
    private InputMultiplexer multiplexer;
    private FoodPlate foodPlate;
    private HUD hud;
    private Vector3 touchPos;
    private Rectangle throwBounds;

    private boolean canThrow;

    boolean wasTouchDragged;

    private int VISITORS_SERVED;
    private int THROW_AMOUNT;

    /**
     * Constructor sets up all the necessary things. Almost nothing is initialized in the show()
     * method and gets initialized by the constructor instead, because this screen can be returned
     * to from the pause menu. This way the state of this screen gets preserved through screen swaps.
     *
     * @param kingsFeast Game object to get access to its and its parent's methods
     */
    GameScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
        levelData = kingsFeast.getLevels().get(kingsFeast.getCurrentLevel());
        batch = kingsFeast.getSpriteBatch();
        world = new World(gravity, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        multiplexer = new InputMultiplexer();

        tiledMap = levelData.getTiledMap();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        BodyBuilder.transformObjectsToBodies(tiledMap, world,
                "collision", "walls", false);

        BodyBuilder.transformObjectsToBodies(tiledMap, world,
                "goal", "goal", true);

        foodPlate = new FoodPlate(levelData, kingsFeast, this);
        touchPos = new Vector3();

        throwBounds = levelData.getTHROW_BOUNDS();
        canThrow = false;
        wasTouchDragged = false;
        VISITORS_SERVED = 0;

        hud = new HUD(batch, kingsFeast,this);
    }

    @Override
    public void show() {
        multiplexer.addProcessor(hud.getStage());
        contactProcessing();
        inputProcessing();

        Gdx.input.setInputProcessor(multiplexer);
        hud.updateI18NBundle();
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        Util.clearScreen();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        drawDebug();
        batch.begin();
        drawSling(false);
        foodPlate.draw(batch, world);
        foodPlate.drawTrajectory(batch, this);
        drawSling(true);
        batch.end();

        update();
        Util.worldStep(world, delta);

        batch.setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        hud.dispose();
        shapeRenderer.dispose();
    }

    /**
     * Called in render(), this method contains all the other methods necessary to be called all the
     * time.
     */
    private void update() {
        enableThrowing();
        foodPlate.destroyBody(world, this);
        foodPlate.checkIfBodyStopped();
        foodPlate.timedCameraReset(this);
        snapCameraToBody();
        handleCameraLimits();
        swapScreen();

        camera.update();
        hud.update();
    }

    /**
     * Sets up input processing through an input multiplexer. Input actions are contained in their
     * own respective methods.
     */
    private void inputProcessing() {
        multiplexer.addProcessor(new InputAdapter() {

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
                handleCameraPanning(tmp, lastTouch, screenX, screenY);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                handleThrowing();
                return true;
            }
        });
    }


    /**
     * Sets up contact processing, for box2d physics. Contact actions are contained in their own
     * respective methods.
     */
    private void contactProcessing() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                setLinearDampingOnCollision(contact);
                handleCollisionWithGoal(contact);
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

    /**
     * Draws the box2d debugging lines, if DEBUG_PHYSICS is true.
     *
     * The ShapeRenderer was used as par of the debugging initially as well, but ended up being an
     * actual feature, due to not having enough time to do things properly.
     * ShapeRenderer draws the lines which come from the sling, so the sling actually looks and
     * behaves a bit like a sling.
     *
     * At the very minimum, the ShapeRenderer stuff should have been separated into a method of
     * their own since it became an actual feature, but I'm leaving this as is, because this is how
     * it ended up being in the released product.
     */
    private void drawDebug() {
        if (DEBUG_PHYSICS) {
            box2DDebugRenderer.render(world, camera.combined);
        }
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.line(Util.convertPixelsToMetres(foodPlate.anchor.x + 95),
                Util.convertPixelsToMetres(foodPlate.anchor.y),
                Util.convertPixelsToMetres(foodPlate.firingPos.x),
                Util.convertPixelsToMetres(foodPlate.firingPos.y),
                com.badlogic.gdx.graphics.Color.FIREBRICK,
                com.badlogic.gdx.graphics.Color.BROWN);

        shapeRenderer.line(Util.convertPixelsToMetres(foodPlate.anchor.x - 95),
                Util.convertPixelsToMetres(foodPlate.anchor.y),
                Util.convertPixelsToMetres(foodPlate.firingPos.x),
                Util.convertPixelsToMetres(foodPlate.firingPos.y),
                com.badlogic.gdx.graphics.Color.FIREBRICK,
                com.badlogic.gdx.graphics.Color.BROWN);

        shapeRenderer.end();
    }

    /**
     * Checks for the limits of the camera and makes sure it stays within the level.
     */
    private void handleCameraLimits() {
        if (camera.position.x - (camera.viewportWidth / 2) <= 0)  {
            camera.position.x = 0 + camera.viewportWidth / 2;
        }

        if (camera.position.y - (camera.viewportHeight / 2) <= 0) {
            camera.position.y = 0 + camera.viewportHeight / 2;
        }

        if (camera.position.x + (camera.viewportWidth / 2) >= levelData.getLEVEL_WIDTH()) {
            camera.position.x = levelData.getLEVEL_WIDTH() - (camera.viewportWidth / 2);
        }

        if (camera.position.y + (camera.viewportHeight / 2) >= levelData.getLEVEL_HEIGHT()) {
            camera.position.y = levelData.getLEVEL_HEIGHT() - (camera.viewportHeight / 2);
        }
    }


    /**
     * Makes the camera follow a thrown foodPlate object.
     */
    private void snapCameraToBody() {
        if (foodPlate.isPlateFlying) {
            camera.position.x = foodPlate.getBody().getWorldCenter().x;
            camera.position.y = foodPlate.getBody().getWorldCenter().y;
        }
    }

    /**
     * Resets the cameras position back to default.
     */
    void cameraReset() {
        camera.position.x = 0 + (camera.viewportWidth / 2);
        camera.position.y = 0 + (camera.viewportHeight / 2);
    }

    /**
     * Handles camera panning, so the player can look around a level.
     *
     * @param tmp  Vector used to calculate the necessary camera translation
     * @param lastTouch Position of the last touch by the user
     * @param screenX X coord of an input event
     * @param screenY Y coord of an input event
     */
    private void handleCameraPanning(Vector3 tmp, Vector3 lastTouch, int screenX, int screenY) {
        float posX = Util.convertMetresToPixels(touchPos.x);
        float posY = Util.convertMetresToPixels(touchPos.y);
        if (!foodPlate.isPlateFlying) {
            if (!throwBounds.contains(posX, posY) && !canThrow) {
                tmp.set(screenX, screenY, 0);
                camera.unproject(tmp);
                tmp.sub(lastTouch).scl(-1f, -1f, 0);
                camera.translate(tmp);
            }
        }
    }

    /**
     * Sets the canThrow flag to true, if the player is touching an area near the food object.
     * This allows the player to perform the throw. Touching elsewhere will just pan the camera.
     */
    private void enableThrowing() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        float posX = Util.convertMetresToPixels(touchPos.x);
        float posY = Util.convertMetresToPixels(touchPos.y);

        if (Gdx.input.justTouched()) {
            if (throwBounds.contains(posX, posY)) {
                canThrow = true;
            }
        }
    }

    /**
     * This input action is called within the touchDragged input event. When the user is dragging on
     * the screen (only when the dragging started near the throwable object, and if there isn't
     * an object flying already!) the necessary calculations for a potential throw are made.
     * Sets the wasTouchDragged flag to true.
     *
     * @param screenX X coord of the input event
     * @param screenY Y coord of the input event
     */
    private void handleThrowCalculations(int screenX, int screenY) {
        if (canThrow && !foodPlate.isPlateFlying) {
            // transforms screen coordinates to game coordinates.
            // InputProcessor returns values with x = 0, y = 0 in top left,
            // so they have to be transformed.
            Vector3 screenCoordinates = new Vector3(screenX, screenY, 0);
            camera.unproject(screenCoordinates);

            // calculates throw based on the dragging.
            foodPlate.calculateAngleAndDistance(screenCoordinates.x,
                    screenCoordinates.y);

            wasTouchDragged = true;
        }
    }

    /**
     * This input action is called within the touchUp input event. When the user stops touching the
     * screen after dragging, a throw is made. Creates a body for the flying object based on prior
     * calculations, and increments THROW_AMOUNT. Resets the relevant flags and the firing position.
     */
    private void handleThrowing() {
        if (canThrow && !foodPlate.isPlateFlying && wasTouchDragged) {
            foodPlate.setBody(foodPlate.createBody(world));

            THROW_AMOUNT++;

            // This just resets the firing position back to the anchor.
            foodPlate.firingPos.set(foodPlate.anchor.cpy());
            canThrow = false;
            wasTouchDragged = false;
        }
    }

    /**
     * Handles the collision between a foodPlate and a 'goal'.
     * If a flying foodPlate object collides with a 'goal' object, the foodPlate physics object gets
     * marked for removal. FoodPlate's recentlyScored flag is set to true, so that the progress in
     * the level gets incremented eventually.
     *
     * The same 'goal' can't get hit again, because it's userData gets changed to something
     * different, in this case, the texture of the foodPlate. The texture gets saved to the userData
     * of that specific goal which was hit, so that the texture can be drawn there. That serves as
     * a visual cue to the player to indicate a 'goal' has already been hit.
     * @param contact Contact object from the contact event.
     */
    private void handleCollisionWithGoal(Contact contact) {
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        // if foodplate collided with goal -> destroy it's body
        if (userDataA.equals("foodPlate") && userDataB.equals("goal") ||
                userDataB.equals("foodPlate") && userDataA.equals("goal")) {

            foodPlate.removeBody = true;
            foodPlate.isPlateFlying = false;
            foodPlate.recentlyScored = true;

            // Foodplate's texture gets saved to the userdata of that goal spot, so that
            // texture can be drawn there. Serves as a visual cue to the player to indicate
            // that that specific spot has been hit already.
            if (userDataA.equals("goal")) {
                contact.getFixtureA().getBody().setUserData(foodPlate.getFoodTexture());
            }
            if (userDataB.equals("goal")) {
                contact.getFixtureB().getBody().setUserData(foodPlate.getFoodTexture());
            }
        }
    }

    /**
     * Whenever a foodPlate hits anything, it starts slowing down. This method does just that, it
     * adds linear damping on the body of foodPlate, whenever it hits a wall.
     * @param contact Contact object from the contact event.
     */
    private void setLinearDampingOnCollision(Contact contact) {
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        if (userDataA.equals("foodPlate") && userDataB.equals("walls") ||
                userDataB.equals("foodPlate") && userDataA.equals("walls")) {

            foodPlate.getBody().setLinearDamping(0.43f);
        }
    }

    // swaps the level to the next one if current one is finished (all objects have been thrown)
    // currently just loops back to the first level after all levels have been completed.

    /**
     * Swaps the screen to the feedback screen if current level is completed
     * (all the guests have been served).
     */
    private void swapScreen() {
        if (VISITORS_SERVED == levelData.getVisitorCount()) {
            dispose();
            kingsFeast.setScreen(new FeedbackScreen(kingsFeast, THROW_AMOUNT, VISITORS_SERVED));
        }
    }

    /**
     * Draws the sling based on where the anchor position is for the throwing mechanic.
     * @param drawInFront There's two parts to the sling, the bit that is supposed to be drawn
     *                    in front of the foodPlate's texture, and the bit that's part of the
     *                    background. This is used to check which one should be drawn.
     */
    private void drawSling(boolean drawInFront) {
        Texture sling;
        if (drawInFront) {
            sling = kingsFeast.getAssetManager().get("ritsa_fground.png");
        } else {
            sling = kingsFeast.getAssetManager().get("ritsa_bground.png");
        }
        float posX = levelData.getSlingAnchorPos().x - (sling.getWidth() / 2f);
        float posY = levelData.getSlingAnchorPos().y - sling.getHeight();
        batch.draw(sling,
                Util.convertPixelsToMetres(posX),
                Util.convertPixelsToMetres(posY + 15),
                Util.convertPixelsToMetres(sling.getWidth()),
                Util.convertPixelsToMetres(sling.getHeight()));
    }

    float getGAME_WIDTH() {
        return GAME_WIDTH;
    }

    float getGAME_HEIGHT() {
        return GAME_HEIGHT;
    }

    void incrementVISITORS_SERVED() {
        this.VISITORS_SERVED++;
    }

    int getVISITORS_SERVED() {
        return VISITORS_SERVED;
    }

    int getTHROW_AMOUNT() {
        return THROW_AMOUNT;
    }

    LevelData getLevelData() {
        return levelData;
    }

    FoodPlate getFoodPlate() {
        return foodPlate;
    }

    Vector2 getGravity() {
        return gravity;
    }

    GameScreen getThis() {
        return this;
    }
}
