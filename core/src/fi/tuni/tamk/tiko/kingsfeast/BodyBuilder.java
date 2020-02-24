package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class BodyBuilder {
    private static float unitScale = 0.0f;

    public static void transformObjectsToBodies(TiledMap tiledMap, World world,
                                                String layer, String userData, float scale) {

        unitScale = scale;
        MapObjects mapObjects = tiledMap.getLayers().get(layer).getObjects();

        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        Array<PolygonMapObject> polygonObjects = mapObjects.getByType(PolygonMapObject.class);
        Array<CircleMapObject> circleObjects = mapObjects.getByType(CircleMapObject.class);

        for (RectangleMapObject rectangleObject : rectangleObjects) {
            createStaticBody(world, getRectangleShape(rectangleObject), userData);
        }

        for (PolygonMapObject polygonObject : polygonObjects) {
            createStaticBody(world, getPolygonShape(polygonObject), userData);
        }

        for (CircleMapObject circleObject : circleObjects) {
            createStaticBody(world, getCircleShape(circleObject), userData);
        }
    }

    private static PolygonShape getRectangleShape(RectangleMapObject rectangleObject) {
        Rectangle r = rectangleObject.getRectangle();
        PolygonShape rectangle = new PolygonShape();
        Vector2 center = new Vector2((r.x + r.width * 0.5f) * unitScale,
                (r.y + r.height * 0.5f) * unitScale);

        rectangle.setAsBox((r.width * 0.5f) * unitScale,
                (r.height * 0.5f) * unitScale, center, 0.0f);

        return rectangle;
    }

    private static PolygonShape getPolygonShape(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();
        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            worldVertices[i] = vertices[i] * unitScale;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static CircleShape getCircleShape(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius * unitScale);
        circleShape.setPosition(new Vector2(circle.x * unitScale, circle.y * unitScale));
        return circleShape;
    }

    private static void createStaticBody(World world, Shape shape, String userData) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bd);
        body.setUserData(userData);
        body.createFixture(shape, 0.0f);

        shape.dispose();
    }
}
