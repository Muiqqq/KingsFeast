package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.graphics.Texture;
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
    private Array<Texture> foodTextures;
    private Array<Texture> visitorTextures;

    LevelData() {
        // Don't know how to utilize this yet.
    }

    void setTiledMap(String mapPath) {
        tiledMap = new TmxMapLoader().load(mapPath);
        LEVEL_WIDTH = Util.getLevelWidth(tiledMap) * unitScale;
    }

    TiledMap getTiledMap() {
        return tiledMap;
    }

    float getLEVEL_WIDTH() {
        return LEVEL_WIDTH;
    }

    // This thing needs to be taken from the tiledMap somehow.
    void setSlingAnchorPos() {
        slingAnchorPos = new Vector2(Util.convertMetresToPixels(1.28f),
                Util.convertMetresToPixels(1.28f));

        // remember to change these to match whatever the bounds are eventually gonna be.
        // currently this is a bad way to do it but oh well.
        THROW_BOUNDS = new Rectangle(slingAnchorPos.x - 128f,
                slingAnchorPos.y - 128f,
                128f, 128f);
    }

    Vector2 getSlingAnchorPos() {
        return slingAnchorPos;
    }

    Rectangle getTHROW_BOUNDS() {
        return THROW_BOUNDS;
    }
}
