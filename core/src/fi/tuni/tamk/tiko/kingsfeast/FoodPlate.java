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

    // These are here just so they can be modified easily.
    private final float MAX_STRENGTH = 5f;
    private final float MAX_DISTANCE = 200f;
    //private final float UPPER_ANGLE = 3 * MathUtils.PI / 2f;
    //private final float LOWER_ANGLE = MathUtils.PI / 2f;
    private final float plateDensity = 2.0f;
    private final float restitution = 0.6f;
    private final float friction = 0.6f;
    private float timePassedInSeconds = 0f;
    private float recentSpeed;

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

    FoodPlate(LevelData levelData, KingsFeast kingsFeast) {
        // anchor pos will come from the slings position once that's implemented.
        this.kingsFeast = kingsFeast;
        this.levelData = levelData;
        anchor = levelData.getSlingAnchorPos();


        randomizeTexture();

        firingPos = anchor.cpy();
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
        /*if (angle > LOWER_ANGLE) {
            if (angle > UPPER_ANGLE) {
                angle = 0;
            } else {
                angle = LOWER_ANGLE;
            }
        } */
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
            MapLayer layer = levelData.getTiledMap().getLayers().get("foodplate");
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

            //float velocityX = Math.abs( (MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 100f)));
            //float velocityY = Math.abs( (MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 100f)));
            float velocityX = -(MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 100f));
            float velocityY = -(MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 100f));

            body.applyLinearImpulse(new Vector2(velocityX, velocityY),
                    body.getWorldCenter(), true);

            // used to check if body has stopped moving so it can be cleared.
            recentSpeed = body.getLinearVelocity().len();

            // debugging stuff
            System.out.println("velocityX: " + velocityX + " + velocityY: " + velocityY);
            System.out.println("angle: " + angle);
            System.out.println("drag distance: " + distance);
            System.out.println("anchor position (pixels): " + anchor);
            System.out.println("release position (pixels): " + firingPos);

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
        foodTextures.add(kingsFeast.getAssetManager().get("fruitSalad.png", Texture.class));
        foodTextures.add(kingsFeast.getAssetManager().get("fruitSalad.png", Texture.class));

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
        MapLayer layer = levelData.getTiledMap().getLayers().get("foodplate");
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
}
