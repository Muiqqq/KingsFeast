package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * All the information that differs from level to level will be held in an object of this
 * class.
 *
 * Amount of food projectiles and visitors etc. will be added here too.
 * This exists so a different screen isn't needed for every single level.
 */
class LevelData {
    private final float unitScale = Util.getUnitScale();
    private TiledMap tiledMap;
    private float LEVEL_WIDTH;
    private Vector2 slingAnchorPos;
    private Rectangle THROW_BOUNDS;
    private int VISITOR_COUNT;

    LevelData(String tiledMapPath) {
        setTiledMap(tiledMapPath);
        setSlingAnchorPos();
        setVisitorCount();
    }

    // Sets the tiled map and the levels width for a level.
    // Levels width is needed for handling camera limits.
    private void setTiledMap(String mapPath) {
        tiledMap = new TmxMapLoader().load(mapPath);
        LEVEL_WIDTH = Util.getLevelWidth(tiledMap) * unitScale;
    }

    TiledMap getTiledMap() {
        return tiledMap;
    }

    float getLEVEL_WIDTH() {
        return LEVEL_WIDTH;
    }

    // This thing needs to be taken from the tiledMap somehow if it's not going to be
    // the same for every level.
    private void setSlingAnchorPos() {
        slingAnchorPos = new Vector2(Util.convertMetresToPixels(1.60f),
                Util.convertMetresToPixels(1.60f));

        // remember to change these to match whatever the bounds are eventually gonna be.
        // currently this is a bad way to do it but oh well.
        THROW_BOUNDS = new Rectangle(slingAnchorPos.x - 160f,
                slingAnchorPos.y - 160f,
                160f, 160f);
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
