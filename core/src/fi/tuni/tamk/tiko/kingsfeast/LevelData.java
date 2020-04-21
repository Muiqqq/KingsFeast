package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A class that stores information about the game's levels. LevelData stores relevant data that
 * differs from level to level.
 *
 * Objects of this class are put in to an array and that creates a neat list
 * where all the levels can be stored.
 */
class LevelData {
    private final float unitScale = Util.getUnitScale();
    private TiledMap tiledMap;
    private float LEVEL_WIDTH;
    private float LEVEL_HEIGHT;
    private Vector2 slingAnchorPos;
    private Rectangle THROW_BOUNDS;
    private int VISITOR_COUNT;

    /**
     * Takes a tiled map, stores it. Info such as level height and width, anchor position,
     * visitor count are taken from the map and then stored.
     *
     * @param tiledMap this map is stored and some important data is taken from it.
     */
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

    /**
     * Gets and sets the 'anchor' position for the throwing mechanic.
     *
     * Anchor pos is taken from the tiled map and then stored as a vector2. Throwing boundaries
     * are set around the anchor pos. THROW_BOUNDS is the area in which the player has to press
     * to initiate a throw.
     */
    private void setSlingAnchorPos() {
        MapObject mapObject = tiledMap.getLayers().get("anchor").getObjects().get(0);
        RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
        slingAnchorPos = new Vector2(rectangleMapObject.getRectangle().getX(),
                rectangleMapObject.getRectangle().getY());

        THROW_BOUNDS = new Rectangle(slingAnchorPos.x - 80f, slingAnchorPos.y - 80f,
                160f, 160f);
    }

    Vector2 getSlingAnchorPos() {
        return slingAnchorPos;
    }

    Rectangle getTHROW_BOUNDS() {
        return THROW_BOUNDS;
    }

    /**
     * Gets the amount of goal objects in the tiled map and sets VISITOR_COUNT to match that.
     * VISITOR_COUNT is used to track player's progress in a level.
     */
    private void setVisitorCount() {
        MapObjects mapObjects = tiledMap.getLayers().get("goal").getObjects();
        Array<RectangleMapObject> visitorObjects = mapObjects.getByType(RectangleMapObject.class);
        VISITOR_COUNT = visitorObjects.size;
    }

    int getVisitorCount() {
        return VISITOR_COUNT;
    }
}
