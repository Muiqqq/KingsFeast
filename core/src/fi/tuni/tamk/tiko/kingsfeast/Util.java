package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Meant to reduce cluttering of other classes.
 */
class Util {
    private static final float unitScale = 1 / 100f;
    private static double accumulator = 0;
    private static final float TIME_STEP = 1 / 60f;

    static float getUnitScale() {
        return unitScale;
    }

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

    static int getLevelHeight(TiledMap tiledMap) {
        MapProperties properties = tiledMap.getProperties();
        int mapHeight = properties.get("height", Integer.class);
        int tilePixelWidth = properties.get("tilewidth", Integer.class);
        return mapHeight * tilePixelWidth;
    }

    static float convertPixelsToMetres(float pixels) {
        return pixels * unitScale;
    }

    static float convertMetresToPixels(float metres) {
        return metres / unitScale;
    }

    static BitmapFont initFont(int fontSize) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SHOWG.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = fontSize;
        fontParameter.borderWidth = 4;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.color = Color.WHITE;
        return fontGenerator.generateFont(fontParameter);
    }

    /**
     * Creates an array containing all the levels in the game.
     *
     * @return an array containing all the levels of the game.
     */
    static Array<LevelData> buildLevels(KingsFeast kingsFeast) {
        Array<LevelData> levels = new Array<>();
        int mapNumber = 1;
        for (int i = 0; i < 13; i++) {
            String mapFileName =  "map" + mapNumber + ".tmx";
            mapNumber++;
            levels.add(new LevelData(kingsFeast.getAssetManager().get(mapFileName,
                    TiledMap.class)));
        }

        return levels;
    }
}
