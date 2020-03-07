package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Class for transforming TiledMap MapObjects to Box2d Bodies. Currently supports
 * transforming into StaticBodies, support for Dynamic and/or Kinematic bodies needs to be added,
 * if needed.
 *
 * Turns out box2d doesn't support ellipses (without turning them into polygons), and Tiled maps
 * can't create straight up circles, only ellipses. -> Circular objects are not currently supported.
 */
class BodyBuilder {
    // unitScale is for pixels -> meters conversion
    private static float unitScale = 0.0f;

    /**
     * Method to iterate through all of the MapObjects in a layer.
     *
     * @param tiledMap Tiled Map to get objects from. These objects get transformed to bodies.
     * @param world Needed to actually create the bodies.
     * @param layer Tiled map layer (specifically, the name of the layer),
     *              from where the objects are taken from.
     * @param userData Userdata to be saved in the bodies currently being created. Used to identify
     *                 bodies from different object layers from one another.
     * @param scale Used to change pixels to meters. Box2d needs meters.
     */
    static void transformObjectsToBodies(TiledMap tiledMap, World world,
                                                String layer, String userData, float scale) {

        unitScale = scale;
        MapObjects mapObjects = tiledMap.getLayers().get(layer).getObjects();

        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        Array<PolygonMapObject> polygonObjects = mapObjects.getByType(PolygonMapObject.class);

        for (RectangleMapObject rectangleObject : rectangleObjects) {
            createStaticBody(world, getRectangleShape(rectangleObject), userData);
        }

        for (PolygonMapObject polygonObject : polygonObjects) {
            createStaticBody(world, getPolygonShape(polygonObject), userData);
        }
    }

    /**
     * Turns a RectangleMapObject into a PolygonShape that represents a rectangle.
     *
     * @param rectangleObject This gets transformed.
     * @return PolygonShape that is actually a rectangle. Used to create a rectangular body later.
     */
    private static PolygonShape getRectangleShape(RectangleMapObject rectangleObject) {
        Rectangle r = rectangleObject.getRectangle();
        PolygonShape rectangle = new PolygonShape();
        Vector2 center = new Vector2((r.x + r.width * 0.5f) * unitScale,
                (r.y + r.height * 0.5f) * unitScale);

        rectangle.setAsBox((r.width * 0.5f) * unitScale,
                (r.height * 0.5f) * unitScale, center, 0.0f);

        return rectangle;
    }

    /**
     * Turns a PolygonMapObject into a PolygonShape.
     *
     * @param polygonObject This gets transformed.
     * @return PolygonShape. Used to create a polygon shaped body later.
     */
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

    /**
     * Creates a body based on a shape created in one of the get methods in this class.
     *
     * @param world World is used to create the body.
     * @param shape This was made in one of the get methods in this class. Used as the shape
     *              for the body that is being created.
     * @param userData Used to identify objects from different object layers from one another.
     */
    private static void createStaticBody(World world, Shape shape, String userData) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bd);
        body.setUserData(userData);
        body.createFixture(shape, 0.0f);

        shape.dispose();
    }
}
