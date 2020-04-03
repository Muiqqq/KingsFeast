package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * All the information that differs from level to level will be held in an object of this
 * class.
 */
class LevelData {
    private final float unitScale = Util.getUnitScale();
    private TiledMap tiledMap;
    private float LEVEL_WIDTH;
    private float LEVEL_HEIGHT;
    private Vector2 slingAnchorPos;
    private Rectangle THROW_BOUNDS;
    private int VISITOR_COUNT;

    LevelData(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        LEVEL_WIDTH = Util.getLevelWidth(tiledMap) * unitScale;
        LEVEL_HEIGHT = Util.getLevelHeight(tiledMap) * unitScale;
        setSlingAnchorPos();
        setVisitorCount();
    }

    TiledMap getTiledMap() {
        return tiledMap;
    }

    float getLEVEL_WIDTH() {
        return LEVEL_WIDTH;
    }

    float getLEVEL_HEIGHT() {
        return LEVEL_HEIGHT;
    }

    // This thing needs to be taken from the tiledMap somehow if it's not going to be
    // the same for every level.
    private void setSlingAnchorPos() {
        MapObject mapObject = tiledMap.getLayers().get("anchor").getObjects().get(0);
        RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
        slingAnchorPos = new Vector2(rectangleMapObject.getRectangle().getX(),
                rectangleMapObject.getRectangle().getY());

        // remember to change these to match whatever the bounds are eventually gonna be.
        // currently this is a bad way to do it but oh well.
        THROW_BOUNDS = new Rectangle(slingAnchorPos.x - 160f,
                slingAnchorPos.y - 160f,
                180f, 180f);
    }

    Vector2 getSlingAnchorPos() {
        return slingAnchorPos;
    }

    Rectangle getTHROW_BOUNDS() {
        return THROW_BOUNDS;
    }

    // Gets the amount of goal objects and sets VISITOR_COUNT to match that.
    // VISITOR_COUNT is used to track players progress in a level.
    private void setVisitorCount() {
        MapObjects mapObjects = tiledMap.getLayers().get("goal").getObjects();
        Array<RectangleMapObject> visitorObjects = mapObjects.getByType(RectangleMapObject.class);
        VISITOR_COUNT = visitorObjects.size;
    }

    int getVisitorCount() {
        return VISITOR_COUNT;
    }
}
