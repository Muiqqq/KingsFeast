package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * TODO: DOCUMENTATION!
 *
 * Has to be refactored based on other additions to code.
 *
 * Restitution and friction can be set with body.getFixture().setRestitution()
 * / body.getFixture().setFriction() in createBody(). Values for those are currently default.
 *
 * Anchor's position is currently set in a dumb, slow to modify way. Look at its
 * initialization to change its position. Will eventually be based on wherever the royal
 * sling is in a given level.
 *
 * Texture etc. could be stored here, as well as everything else a food plate could need.
 * Documentation is work in progress.
 */
class FoodPlate {
    private final float MAX_STRENGTH = 15f;
    private final float MAX_DISTANCE = 100f;
    private final float UPPER_ANGLE = 3 * MathUtils.PI / 2f;
    private final float LOWER_ANGLE = MathUtils.PI / 2f;
    private final float plateRadius = 0.10f;
    private final float plateDensity = 2.0f;

    private float recentSpeed;

    Vector2 anchor;
    Vector2 firingPos;
    private float distance;
    private float angle;

    private Array<Texture> foodTextures;

    Body body;
    boolean isPlateFlying = false;
    boolean removeBody = false;
    boolean allPlatesThrown = false;

    FoodPlate(Vector2 anchor, Array<Texture> foodTextures) {
        // anchor pos will come from the slings position once that's implemented.
        this.anchor = anchor;
        this.foodTextures = foodTextures;

        System.out.println(this.anchor);

        firingPos = this.anchor.cpy();
    }

    // Gets the angle between two points
    private float angleBetweenTwoPoints() {
        float angle = MathUtils.atan2(anchor.y - firingPos.y, anchor.x - firingPos.x);
        angle %= 2 * MathUtils.PI;
        if (angle < 0) {
            angle += 2 * MathUtils.PI2;
        }
        return angle;
    }

    // Gets the distance between two points
    private float distanceBetweenTwoPoints() {
        return (float) Math.sqrt(((anchor.x - firingPos.x) * (anchor.x - firingPos.x)) +
                ((anchor.y - firingPos.y) * (anchor.y - firingPos.y)));
    }

    // Calculates angle and distance of the throw
    void calculateAngleAndDistance(float screenX, float screenY, float unitScale) {
        firingPos.set(Util.convertMetresToPixels(screenX),
                Util.convertMetresToPixels(screenY));
        distance = distanceBetweenTwoPoints();
        angle = angleBetweenTwoPoints();

        if (distance > MAX_DISTANCE) {
            distance = MAX_DISTANCE;
        }
        if (angle > LOWER_ANGLE) {
            if (angle > UPPER_ANGLE) {
                angle = 0;
            } else {
                angle = LOWER_ANGLE;
            }
        }
        firingPos.set(anchor.x + (distance * -MathUtils.cos(angle)),
                anchor.y + (distance * -MathUtils.sin(angle)));
    }

    // Creates a body with a velocity based on calculations above.
    Body createBody(World world) {
        if (!isPlateFlying) {
            isPlateFlying = true;
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(plateRadius);
            circleShape.setPosition(new Vector2(Util.convertPixelsToMetres(firingPos.x),
                    Util.convertPixelsToMetres(firingPos.y)));

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.DynamicBody;
            Body body = world.createBody(bd);
            body.setUserData("foodPlate");
            body.createFixture(circleShape, plateDensity);
            body.getFixtureList().get(0).setFriction(0.1f);
            circleShape.dispose();

            float velocityX = Math.abs( (MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 100f)));
            float velocityY = Math.abs( (MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 100f)));
            body.setLinearVelocity(velocityX, velocityY);

            // This slows the velocity by the specified amount every time step. Value we should use
            // has to be tested through trial and error.
            body.setLinearDamping(0.33f);

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
            if (recentSpeed < 0.075f) {
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
            gameScreen.cameraReset();

            // TEMPORARY!!
            allPlatesThrown = true;
        }
    }
}
