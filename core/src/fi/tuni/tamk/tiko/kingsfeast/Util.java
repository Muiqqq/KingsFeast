package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Supposed to contain universally useful methods. Meant to reduce cluttering
 * of KingsFeast class.
 */
class Util {
    private static final float unitScale = 1 / 100f;
    private static double accumulator = 0;
    private static float TIME_STEP = 1 / 60f;

    static float getUnitScale() {
        return unitScale;
    }

    // Jussi's method for world stepping
    static void worldStep(World world, float deltaTime) {
        float frameTime = deltaTime;

        if(deltaTime > 1 / 4f) {
            frameTime = 1 / 4f;
        }

        accumulator += frameTime;

        while(accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
        }
    }

    static void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    static int getLevelWidth(TiledMap tiledMap) {
        MapProperties properties = tiledMap.getProperties();
        int mapWidth = properties.get("width", Integer.class);
        int tilePixelWidth = properties.get("tilewidth", Integer.class);
        return mapWidth * tilePixelWidth;
    }

    static float convertPixelsToMetres(float pixels) {
        return pixels * unitScale;
    }

    static float convertMetresToPixels(float metres) {
        return metres / unitScale;
    }
}
