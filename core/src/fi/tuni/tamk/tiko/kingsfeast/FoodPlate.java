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
 * TODO: DOCUMENTATION!
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

    FoodPlate(LevelData levelData, KingsFeast kingsFeast, GameScreen gameScreen) {
        // anchor pos will come from the slings position once that's implemented.
        this.kingsFeast = kingsFeast;
        this.levelData = levelData;
        anchor = levelData.getSlingAnchorPos();
        firingPos = anchor.cpy();
        randomizeTexture();
        gravity = gameScreen.getGravity();
    }

    // Gets the angle between two points
    private float angleBetweenTwoPoints() {
        float angle = MathUtils.atan2(anchor.y - firingPos.y, anchor.x - firingPos.x);
        angle %= 2 * MathUtils.PI;
        /*if (angle < 0) {
            angle += 2 * MathUtils.PI2;
        }*/
        return angle;
    }

    // Gets the distance between two points
    private float distanceBetweenTwoPoints() {
        return (float) Math.sqrt(((anchor.x - firingPos.x) * (anchor.x - firingPos.x)) +
                ((anchor.y - firingPos.y) * (anchor.y - firingPos.y)));
    }

    // Calculates angle and distance of the throw
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

    // Creates a body with a velocity based on calculations above.
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
                System.out.println("flatplate");
            } else {
                layer = levelData.getTiledMap().getLayers().get("bowlplate");
                System.out.println("bowlplate");
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

    // if plate has almost stopped -> mark it for removal.
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

    // removes the plate body so game can continue
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

    // Sets a random texture for the foodplate. Adds a bit of visual flavor.
    // Refactor depending on how texture files / atlas is being handled.
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

    // TODO: add rotation for flying food
    //  - fine tune size etc, make sure textures don't float on top of or sink
    //  - into platforms/floors etc.
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

    private Vector2 getStartingVelocity() {
        float velocityX = -(MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 5.7f));
        float velocityY = -(MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 5.7f));
        return new Vector2(velocityX, velocityY);
    }

    private float getTrajectoryX(float t) {
        return getStartingVelocity().x * t + anchor.x;
    }

    private float getTrajectoryY(float t) {
        return 0.5f * gravity.y * t * t + getStartingVelocity().y * t + anchor.y;
    }

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
