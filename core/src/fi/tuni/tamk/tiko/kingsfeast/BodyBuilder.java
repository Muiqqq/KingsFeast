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
 */
class BodyBuilder {
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
     */
    static void transformObjectsToBodies(TiledMap tiledMap, World world,
                                                String layer, String userData, boolean isSensor) {

        unitScale = Util.getUnitScale();
        MapObjects mapObjects = tiledMap.getLayers().get(layer).getObjects();

        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        Array<PolygonMapObject> polygonObjects = mapObjects.getByType(PolygonMapObject.class);

        for (RectangleMapObject rectangleObject : rectangleObjects) {
            createStaticBody(world,
                    getRectangleShape(rectangleObject),
                    createRectangleBodyDef(rectangleObject),
                    userData,
                    isSensor);
        }

        for (PolygonMapObject polygonObject : polygonObjects) {
            createStaticBody(world,
                    getPolygonShape(polygonObject),
                    createPolygonBodyDef(polygonObject),
                    userData,
                    isSensor);
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
        rectangle.setAsBox((r.width * 0.5f) * unitScale,
                (r.height * 0.5f) * unitScale);

        return rectangle;
    }

    /**
     * Creates a bodyDef for rectangleObjects. It's important that the location is set for the
     * body and not it's fixture/shape.
     * @param rectangleObject Tiled Map mapObject from which to get all the necessary data from.
     * @return BodyDef for a rectangular StaticBody.
     */
    private static BodyDef createRectangleBodyDef(RectangleMapObject rectangleObject) {
        Rectangle r = rectangleObject.getRectangle();
        Vector2 center = new Vector2((r.x + r.width * 0.5f) * unitScale,
                (r.y + r.height * 0.5f) * unitScale);

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(center);
        return bd;
    }

    /**
     * Turns a PolygonMapObject into a PolygonShape.
     *
     * @param polygonObject This gets transformed.
     * @return PolygonShape. Used to create a polygon shaped body later.
     */
    static PolygonShape getPolygonShape(PolygonMapObject polygonObject) {
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
     * Currently redundant, exists for the sake of consistency.
     * Should be made to store the location like rectangular polygons do in the
     * createRectangleBodyDef() method. Not necessary for functionality, will be changed
     * later if there's time for it.
     *
     * @param polygonObject Does nothing
     * @return Empty BodyDef of the type StaticBody.
     */
    private static BodyDef createPolygonBodyDef(PolygonMapObject polygonObject) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;

        return bd;
    }

    /**
     * Creates a body based on a shape created in one of the get methods in this class.
     *
     * @param world World is used to create the body.
     * @param shape This was made in one of the get methods in this class. Used as the shape
     *              for the body that is being created.
     * @param userData Used to identify objects from different object layers from one another.
     */
    private static void createStaticBody(World world, Shape shape, BodyDef bd,
                                         String userData, boolean isSensor) {

        Body body = world.createBody(bd);
        body.setUserData(userData);
        body.createFixture(shape, 1.0f);

        body.getFixtureList().get(0).setSensor(isSensor);

        shape.dispose();
    }
}
