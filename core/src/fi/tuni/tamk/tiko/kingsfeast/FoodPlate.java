package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * This class contains things that have something to do with the object that gets thrown around the
 * levels in the game. A lot of the calculations needed for the throwing mechanic happen with
 * methods from this class. Also, methods for manipulating the object, such as creating and handling
 * the destruction of the box2d physics body etc., are found here.
 *
 */
class FoodPlate {
    private final KingsFeast kingsFeast;
    private final LevelData levelData;

    private final float MAX_STRENGTH = 5f;
    private final float MAX_DISTANCE = 200f;
    private final float plateDensity = 2.0f;
    private final float restitution = 0.6f;
    private final float friction = 0.6f;
    private float timePassedInSeconds = 0f;
    private float recentSpeed;
    private Vector2 gravity;

    private Sound throwSound;

    Vector2 anchor;
    Vector2 firingPos;
    private float distance;
    private float angle;

    private Texture foodTexture;

    private PolygonShape polygon;
    private Body body;
    boolean isPlateFlying = false;
    boolean removeBody = false;
    boolean recentlyScored = false;

    /**
     * Relevant data is stored with constructor. Also randomizes the first texture that is to be
     * used.
     * @param levelData LevelData object stores some information about the level
     *                 that is needed by this class
     * @param kingsFeast Game object, used to access its and its parent's methods
     * @param gameScreen GameScreen object, used to access its and its parent's methods
     */
    FoodPlate(LevelData levelData, KingsFeast kingsFeast, GameScreen gameScreen) {
        // anchor pos will come from the slings position once that's implemented.
        this.kingsFeast = kingsFeast;
        this.levelData = levelData;
        anchor = levelData.getSlingAnchorPos();
        firingPos = anchor.cpy();
        randomizeTexture();
        gravity = gameScreen.getGravity();
    }

    /**
     * Calculates the angle between the throwing anchor point and the firing position.
     * @return Angle between two points
     */
    private float angleBetweenTwoPoints() {
        float angle = MathUtils.atan2(anchor.y - firingPos.y, anchor.x - firingPos.x);
        angle %= 2 * MathUtils.PI;
        return angle;
    }

    /**
     * Calculates the distance between the throwing anchor point and the firing position.
     * @return Distance between two points (in pixels)
     */
    private float distanceBetweenTwoPoints() {
        return (float) Math.sqrt(((anchor.x - firingPos.x) * (anchor.x - firingPos.x)) +
                ((anchor.y - firingPos.y) * (anchor.y - firingPos.y)));
    }

    /**
     * Sets angle and distance based on their respective methods, based on the firing position.
     * Distance can't be bigger than max distance.
     * @param screenX Screen X coordinate for the firing pos.
     * @param screenY Screen Y coordinate for the firing pos.
     */
    void calculateAngleAndDistance(float screenX, float screenY) {
        firingPos.set(Util.convertMetresToPixels(screenX),
                Util.convertMetresToPixels(screenY));
        distance = distanceBetweenTwoPoints();
        angle = angleBetweenTwoPoints();

        if (distance > MAX_DISTANCE) {
            distance = MAX_DISTANCE;
        }

        firingPos.set(anchor.x + (distance * -MathUtils.cos(angle)),
                anchor.y + (distance * -MathUtils.sin(angle)));
    }

    /**
     * Creates the physics body. A body can't be created if one already exists. The body gets its
     * shape from the tiled map. A linear impulse is applied, with velocity based on the necessary
     * calculations.
     * @param world The body is created to be a part of this world
     * @return A body for the foodPlate
     */
    Body createBody(World world) {
        if (!isPlateFlying) {
            isPlateFlying = true;

            //If sounds enabled, play a sound effect when throwing:
            if (kingsFeast.isSoundEffectsEnabled()) {
                int rnd = MathUtils.random(1, 4);
                throwSound = kingsFeast.getAssetManager().get("throw" + rnd + ".mp3");
                throwSound.play(0.3f);
            }

            // Dollar store version of creating a polygon for the food plate
            // Yay for cutting corners
            MapLayer layer;
            if (foodTexture.toString().contains("_flat")) {
                layer = levelData.getTiledMap().getLayers().get("flatplate");
            } else {
                layer = levelData.getTiledMap().getLayers().get("bowlplate");
            }
            PolygonMapObject polyObj = layer.getObjects().getByType(PolygonMapObject.class).get(0);
            polygon = BodyBuilder.getPolygonShape(polyObj);

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.DynamicBody;
            Body body = world.createBody(bd);
            body.setUserData("foodPlate");
            body.createFixture(polygon, plateDensity);
            body.getFixtureList().get(0).setFriction(friction);
            body.getFixtureList().get(0).setRestitution(restitution);
            polygon.dispose();

            float velocityX = -(MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 100f));
            float velocityY = -(MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 100f));

            body.applyLinearImpulse(new Vector2(velocityX, velocityY),
                    body.getWorldCenter(), true);

            // used to check if body has stopped moving so it can be cleared.
            recentSpeed = body.getLinearVelocity().len();

            return body;
        } else {
            return null;
        }
    }

    /**
     * A method used to check if a foodPlate physics body has almost stopped. Marks body for removal
     * if it has almost stopped.
     */
    void checkIfBodyStopped() {
        if (isPlateFlying) {
            float currentSpeed = body.getLinearVelocity().len();
            recentSpeed = 0.1f * currentSpeed + 0.9f * recentSpeed;
            if (recentSpeed < 0.2f) {
                removeBody = true;
                isPlateFlying = false;
            }
        }
    }

    /**
     * If body has been marked for removal, this method is used to destroy it.
     * @param world The world to destroy the body from
     * @param gameScreen Used to call for a camera reset
     */
    void destroyBody(World world, GameScreen gameScreen) {
        if (removeBody) {
            world.destroyBody(body);
            removeBody = false;
            randomizeTexture();
            if(!recentlyScored) {
                gameScreen.cameraReset();
            }
        }
    }

    /**
     * If the player 'scores' (serves a guest), the game waits for a bit before resetting the camera.
     * This way the player gets to see they actually succeeded.
     * @param gameScreen Two methods from this object are needed here.
     */
    void timedCameraReset(GameScreen gameScreen) {
        if (recentlyScored) {
            float timePeriod = 1.499f;
            timePassedInSeconds += Gdx.graphics.getDeltaTime();
            if(timePassedInSeconds > timePeriod) {
                timePassedInSeconds -= timePeriod;
                randomizeTexture();
                recentlyScored = false;
                gameScreen.incrementVISITORS_SERVED();
                gameScreen.cameraReset();
            }
        }
    }

    void setBody(Body body) {
        this.body = body;
    }

    Body getBody() {
        return body;
    }


    /**
     * Sets a random texture for the foodPlate to add a bit of visual flavor.
     */
    private void randomizeTexture() {
        Array<Texture> foodTextures = new Array<>();
        foodTextures.add(kingsFeast.getAssetManager().get("fruitSalad.png", Texture.class));
        foodTextures.add(kingsFeast.getAssetManager().get("kana_flat.png", Texture.class));
        foodTextures.add(kingsFeast.getAssetManager().get("keitto.png", Texture.class));
        foodTextures.add(kingsFeast.getAssetManager().get("spagetti_flat.png", Texture.class));
        foodTextures.add(kingsFeast.getAssetManager().get("kakku_flat.png", Texture.class));
        foodTextures.add(kingsFeast.getAssetManager().get("puuro.png", Texture.class));

        foodTexture = foodTextures.get(MathUtils.random(0, foodTextures.size - 1));
    }

    Texture getFoodTexture() {
        return foodTexture;
    }

    /**
     * Draws all the different states for the foodPlate.
     *
     * Draws the correct texture in front of every guest that has been served. When a food that is
     * thrown hits a 'goal', the texture of it gets saved to the userdata of that 'goal'. That
     * texture is then drawn.
     *
     * Draws the texture for the flying physics body. If there is no flying physics body, the
     * texture gets drawn on the sling, wherever the current firing position is. Firing position
     * follows touch when dragging, and so does the texture.
     *
     * @param batch Batch is required to draw
     * @param world World is needed so the bodies can be fetched
     */
    void draw(SpriteBatch batch, World world) {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        MapLayer layer = levelData.getTiledMap().getLayers().get("bowlplate");
        PolygonMapObject polyObj = layer.getObjects().getByType(PolygonMapObject.class).get(0);
        float width = Util.convertPixelsToMetres
                (polyObj.getPolygon().getBoundingRectangle().getWidth());

        float height = Util.convertPixelsToMetres
                (polyObj.getPolygon().getBoundingRectangle().getHeight());

        // If a 'visitor' got their food already, draws the food texture at their spot.
        // The texture of the foodplate which collided with a visitor's spot gets saved
        // to the userData of said spot (goal).
        for (Body body : bodies) {
            if (body.getUserData() instanceof Texture) {
                batch.draw((Texture) body.getUserData(),
                        body.getWorldCenter().x - width / 2,
                        body.getWorldCenter().y - height / 2,
                        width, height);
            }
        }

        // If a plate is thrown, draws the texture on the flying physics body,
        // otherwise draws it where the firing position is -> follows touch
        if(isPlateFlying) {
            batch.draw(foodTexture,
                   body.getWorldCenter().x - width / 2,
                    body.getWorldCenter().y - height / 2,
                    width / 2f,
                    height / 2f,
                    width,
                    height,
                    1.0f,
                    1.0f,
                    body.getAngle() * MathUtils.radDeg,
                    0,
                    0,
                    foodTexture.getWidth(),
                    foodTexture.getHeight(),
                    false,
                    false);
        } else {
            batch.draw(foodTexture,
                    Util.convertPixelsToMetres(firingPos.x) - width / 2,
                    Util.convertPixelsToMetres(firingPos.y) - height / 2,
                    width, height);
        }
    }

    /**
     * A hacked together method to get the correct (or rather, a close enough correct) velocity
     * to draw the projectile trajectory. It's not exact, but it's close enough. Almost identical
     * to the way velocity is calculated for the body of foodPlate, but for some reason the same
     * calculation returned a very different result when testing.
     *
     * @return Velocity as a Vector2 for drawing the projectile trajectory
     */
    private Vector2 getStartingVelocity() {
        float velocityX = -(MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 5.7f));
        float velocityY = -(MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 5.7f));
        return new Vector2(velocityX, velocityY);
    }

    /**
     * Calculates a X coordinate at a specific time
     * @param t time in seconds
     * @return X coordinate at a specific time
     */
    private float getTrajectoryX(float t) {
        return getStartingVelocity().x * t + anchor.x;
    }

    /**
     * Calculates an Y coordinate at a specific time
     * @param t time in seconds
     * @return Y coordinate at a specific time
     */
    private float getTrajectoryY(float t) {
        return 0.5f * gravity.y * t * t + getStartingVelocity().y * t + anchor.y;
    }

    /**
     * Method to draw the projectile trajectory used to assist throwing. Only draws when the player
     * is in the process of making a throw.
     *
     * @param sb batch is required to draw
     * @param gameScreen GameScreen object to access its methods and variables
     */
    void drawTrajectory(SpriteBatch sb, GameScreen gameScreen) {
        if (gameScreen.wasTouchDragged) {
            Texture texture = kingsFeast.getAssetManager().get("dot.png");

            float t = 0;
            float timeStep = 1 / 2f;
            int trajectoryPointCount = 6;

            for (int i = 0; i < trajectoryPointCount; i++) {
                float x = getTrajectoryX(t);
                float y = getTrajectoryY(t);
                x = Util.convertPixelsToMetres(x);
                y = Util.convertPixelsToMetres(y);
                sb.draw(texture, x, y, 0.16f, 0.16f);
                t += timeStep;
            }
        }
    }
}
