package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.utils.Array;

/**
 * Create new levels here.
 * Levels contain a different tiled map and other level specific information.
 * This class builds an array containing all the data for all the levels in the game.
 * New levels have to be added by hand.
 *
 *
 * This exists so a different Screen isn't needed for every single level.
 *
 * TODO: more documentation
 */
class LevelBuilder {

    static Array<LevelData> buildLevels() {
        Array<LevelData> levels = new Array<>();
        levels.add(level1());
        levels.add(level2());
        levels.add(level3());
        return levels;
    }

    private static LevelData level1() {
        LevelData level = new LevelData();
        level.setTiledMap("level1.tmx");
        level.setSlingAnchorPos();
        return level;
    }

    private static LevelData level2() {
        LevelData level = new LevelData();
        level.setTiledMap("level2.tmx");
        level.setSlingAnchorPos();
        return level;
    }

    private static LevelData level3() {
        LevelData level = new LevelData();
        level.setTiledMap("level3.tmx");
        level.setSlingAnchorPos();
        return level;
    }
}
