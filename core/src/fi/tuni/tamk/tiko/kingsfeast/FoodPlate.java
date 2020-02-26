package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * TODO: DOCUMENTATION!
 *
 * Has to be refactored based on other additions to code.
 */
public class FoodPlate {
    private final float MAX_STRENGTH = 15f;
    private final float MAX_DISTANCE = 100f;
    private final float UPPER_ANGLE = 3 * MathUtils.PI / 2f;
    private final float LOWER_ANGLE = MathUtils.PI / 2f;
    private final float plateRadius = 0.10f;
    private final float plateDensity = 2.0f;

    Vector2 anchor;
    Vector2 firingPos;
    private float distance;
    private float angle;

    public FoodPlate(float unitScale) {
        // anchor pos will come from the slings position once that's implemented.
        anchor = new Vector2(Util.convertMetresToPixels(1.28f, unitScale),
                Util.convertMetresToPixels(1.28f, unitScale));

        System.out.println(anchor);

        firingPos = anchor.cpy();
    }

    private float angleBetweenTwoPoints() {
        float angle = MathUtils.atan2(anchor.y - firingPos.y, anchor.x - firingPos.x);
        angle %= 2 * MathUtils.PI;
        if (angle < 0) {
            angle += 2 * MathUtils.PI2;
        }
        return angle;
    }

    private float distanceBetweenTwoPoints() {
        return (float) Math.sqrt(((anchor.x - firingPos.x) * (anchor.x - firingPos.x)) +
                ((anchor.y - firingPos.y) * (anchor.y - firingPos.y)));
    }

    public void calculateAngleAndDistance(float screenX, float screenY, float unitScale) {
        firingPos.set(Util.convertMetresToPixels(screenX, unitScale),
                Util.convertMetresToPixels(screenY, unitScale));
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

    public void createBody(World world, float unitScale) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(plateRadius);
        circleShape.setPosition(new Vector2(Util.convertPixelsToMetres(firingPos.x, unitScale),
                Util.convertPixelsToMetres(firingPos.y, unitScale)));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bd);
        body.createFixture(circleShape, plateDensity);
        circleShape.dispose();

        float velocityX = Math.abs( (MAX_STRENGTH * -MathUtils.cos(angle) * (distance / 100f)));
        float velocityY = Math.abs( (MAX_STRENGTH * -MathUtils.sin(angle) * (distance / 100f)));
        body.setLinearVelocity(velocityX, velocityY);

        System.out.println("velocityX: " + velocityX + " + velocityY: " + velocityY);
        System.out.println("angle: " + angle);
        System.out.println("drag distance: " + distance);
        System.out.println("anchor position: " + anchor);
        System.out.println("release position: " + firingPos);
    }
}
