package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Supposed to contain universally useful methods. Meant to reduce cluttering
 * of KingsFeast class.
 */
public class Util {

    private static double accumulator = 0;
    private static float TIME_STEP = 1 / 60f;
    // Jussi's method for world stepping
    public static void worldStep(World world, float deltaTime) {
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

    public static void clearScreen() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static float convertPixelsToMetres(float pixels, float unitScale) {
        return pixels * unitScale;
    }

    public static float convertMetresToPixels(float metres, float unitScale) {
        return metres / unitScale;
    }
}
